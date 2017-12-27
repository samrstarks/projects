package edu.berkeley.cs186.database.query;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import edu.berkeley.cs186.database.Database;
import edu.berkeley.cs186.database.DatabaseException;
import edu.berkeley.cs186.database.common.BacktrackingIterator;
import edu.berkeley.cs186.database.databox.DataBox;
import edu.berkeley.cs186.database.io.Page;
import edu.berkeley.cs186.database.table.Record;
import edu.berkeley.cs186.database.table.Schema;
import edu.berkeley.cs186.database.table.Table;

import javax.xml.crypto.Data;

public class PNLJOperator extends JoinOperator {

  private QueryOperator leftSource;
  private QueryOperator rightSource;
  private int leftColumnIndex;
  private int rightColumnIndex;
  private String leftColumnName;
  private String rightColumnName;
  private Database.Transaction transaction;

  public PNLJOperator(QueryOperator leftSource,
                      QueryOperator rightSource,
                      String leftColumnName,
                      String rightColumnName,
                      Database.Transaction transaction) throws QueryPlanException, DatabaseException {
    super(leftSource,
          rightSource,
          leftColumnName,
          rightColumnName,
          transaction,
          JoinType.PNLJ);

    this.leftSource = getLeftSource();
    this.rightSource = getRightSource();
    this.leftColumnIndex = getLeftColumnIndex();
    this.rightColumnIndex = getRightColumnIndex();
    this.leftColumnName = getLeftColumnName();
    this.rightColumnName = getRightColumnName();
    this.transaction = getTransaction();
  }

  public Iterator<Record> iterator() throws QueryPlanException, DatabaseException {
    return new PNLJIterator();
  }


  /**
   * An implementation of Iterator that provides an iterator interface for this operator.
   */
  private class PNLJIterator extends JoinIterator {
    private Iterator<Record> leftIterator;
    private Iterator<Record> rightIterator;

    private Iterator<Page> leftPageIter;
    private Iterator<Page> rightPageIter;

    private Record leftRecord;
    private Record nextRecord;

    private Page leftPage;
    private Page rightPage;

    public PNLJIterator() throws QueryPlanException, DatabaseException {
      this.leftPageIter = PNLJOperator.this.getPageIterator(PNLJOperator.this.leftColumnName);
      this.leftIterator = PNLJOperator.this.getBlockIterator(PNLJOperator.this.leftColumnName, leftPageIter, 1);

      this.rightPageIter = null;
      this.rightIterator = null;

      this.leftRecord = null;
      this.nextRecord = null;
    }

    public boolean hasNext() {
      if (this.nextRecord != null) {
        return true;
      } else {
        return false;
      }
    }

    /**
     * Yields the next record of this iterator.
     *
     * @return the next Record
     * @throws NoSuchElementException if there are no more Records to yield
     */
    public Record next() {
      if (this.hasNext()) {
        Record r = this.nextRecord;
        this.nextRecord = null;
        return r;
      }
      throw new NoSuchElementException();
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
