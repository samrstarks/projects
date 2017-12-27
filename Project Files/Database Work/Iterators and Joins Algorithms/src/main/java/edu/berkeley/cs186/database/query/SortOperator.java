package edu.berkeley.cs186.database.query;

import com.sun.org.apache.regexp.internal.RE;
import edu.berkeley.cs186.database.Database;
import edu.berkeley.cs186.database.DatabaseException;
import edu.berkeley.cs186.database.common.BacktrackingIterator;
import edu.berkeley.cs186.database.databox.DataBox;
import edu.berkeley.cs186.database.table.Record;
import edu.berkeley.cs186.database.table.Schema;
import edu.berkeley.cs186.database.common.Pair;
import edu.berkeley.cs186.database.io.Page;

import java.util.*;


public class SortOperator  {
  private Database.Transaction transaction;
  private String tableName;
  private Comparator<Record> comparator;
  private Schema operatorSchema;
  private int numBuffers;

  public SortOperator(Database.Transaction transaction, String tableName, Comparator<Record> comparator) throws DatabaseException, QueryPlanException {
    this.transaction = transaction;
    this.tableName = tableName;
    this.comparator = comparator;
    this.operatorSchema = this.computeSchema();
    this.numBuffers = this.transaction.getNumMemoryPages();
  }

  public Schema computeSchema() throws QueryPlanException {
    try {
      return this.transaction.getFullyQualifiedSchema(this.tableName);
    } catch (DatabaseException de) {
      throw new QueryPlanException(de);
    }
  }


  public class Run {
    private String tempTableName;

    public Run() throws DatabaseException {
      this.tempTableName = SortOperator.this.transaction.createTempTable(SortOperator.this.operatorSchema);
    }

    public void addRecord(List<DataBox> values) throws DatabaseException {
      SortOperator.this.transaction.addRecord(this.tempTableName, values);
    }

    public void addRecords(List<Record> records) throws DatabaseException {
      for (Record r: records) {
        this.addRecord(r.getValues());
      }
    }

    public Iterator<Record> iterator() throws DatabaseException {
      return SortOperator.this.transaction.getRecordIterator(this.tempTableName);
    }

    public String tableName() {
      return this.tempTableName;
    }
  }


  /**
   * Returns a NEW run that is the sorted version of the input run.
   * Can do an in memory sort over all the records in this run
   * using one of Java's built-in sorting methods.
   * Note: Don't worry about modifying the original run.
   * Returning a new run would bring one extra page in memory beyond the
   * size of the buffer, but it is done this way for ease.
   */
  public Run sortRun(Run run) throws DatabaseException {
    Iterator<Record> runIter = run.iterator();
    List<Record> runArray = new ArrayList<>();
    Run newRun = new Run();
    while (runIter.hasNext()) {
      runArray.add(runIter.next());
    }
    Collections.sort(runArray, this.comparator);
    newRun.addRecords(runArray);
    return newRun;
  }



  /**
   * Given a list of sorted runs, returns a new run that is the result
   * of merging the input runs. You should use a Priority Queue (java.util.PriorityQueue)
   * to determine which record should be should be added to the output run next.
   * It is recommended that your Priority Queue hold Pair<Record, Integer> objects
   * where a Pair (r, i) is the Record r with the smallest value you are
   * sorting on currently unmerged from run i.
   */
  public Run mergeSortedRuns(List<Run> runs) throws DatabaseException {
    Run newRun = new Run();
    Comparator<Pair<Record, Integer>> comparat = new RecordPairComparator();
    PriorityQueue<Pair<Record, Integer>> queue = new PriorityQueue<Pair<Record, Integer>>(runs.size(), comparat);
    ArrayList<Iterator> input_runs = new ArrayList<>();

    for (int i = 0; i < runs.size(); i++) {
      Iterator<Record> iters = runs.get(i).iterator();
      input_runs.add(iters);
    }

    for (int i = 0; i < input_runs.size(); i++) {
      if (input_runs.get(i).hasNext()) {
        Pair<Record, Integer> qAdd = new Pair(input_runs.get(i).next(), i);
        queue.add(qAdd);
      }
    }

    ArrayList<Record> records = new ArrayList<>();
    while (!queue.isEmpty()) {
      Pair<Record, Integer> curr = queue.poll();
      records.add(curr.getFirst());
      if (input_runs.get(curr.getSecond()).hasNext()) {
        Pair added = new Pair(input_runs.get(curr.getSecond()).next(), curr.getSecond());
        queue.add(added);
      }
    }

    newRun.addRecords(records);
    return newRun;
  }

  /**
   * Given a list of N sorted runs, returns a list of
   * sorted runs that is the result of merging (numBuffers - 1)
   * of the input runs at a time.
   */
  public List<Run> mergePass(List<Run> runs) throws DatabaseException {
    Run sorted;
    ArrayList<Run> mergedRuns = new ArrayList();
    int N = runs.size();
    int index = 0;

    while (index < N) {
      ArrayList<Run> merged = new ArrayList();
      for (int j = 0; j < numBuffers - 1; j++) {
        merged.add(runs.get(index));
        index++;
      }
      sorted = mergeSortedRuns(merged);
      mergedRuns.add(sorted);
    }

    return mergedRuns;
  }


  /**
   * Does an external merge sort on the table with name tableName
   * using numBuffers.
   * Returns the name of the table that backs the final run.
   */
  public String sort() throws DatabaseException {
    return new String();
  }

  private class RecordPairComparator implements Comparator<Pair<Record, Integer>> {
    public int compare(Pair<Record, Integer> o1, Pair<Record, Integer> o2) {
      return SortOperator.this.comparator.compare(o1.getFirst(), o2.getFirst());
    }
  }

  public Run createRun() throws DatabaseException {
    return new Run();
  }
}
