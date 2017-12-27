import com.sun.org.apache.regexp.internal.RE;
import javafx.collections.transformation.TransformationList;
import javafx.scene.control.Tab;

import java.util.*;

/**
 * The Lock Manager handles lock and unlock requests from transactions. The
 * Lock Manager will maintain a hashtable that is keyed on the name of the
 * table being locked. The Lock Manager will also keep a FIFO queue of requests
 * for locks that cannot be immediately granted.
 */
public class LockManager {
    private DeadlockAvoidanceType deadlockAvoidanceType;
    private HashMap<String, TableLock> tableToTableLock;

    public enum DeadlockAvoidanceType {
        None,
        WaitDie,
        WoundWait
    }

    public enum LockType {
        Shared,
        Exclusive
    }

    public LockManager(DeadlockAvoidanceType type) {
        this.deadlockAvoidanceType = type;
        this.tableToTableLock = new HashMap<String, TableLock>();
    }

    /**
     * The acquire method will grant the lock if it is compatible. If the lock
     * is not compatible, then the request will be placed on the requesters
     * queue. Once you have implemented deadlock avoidance algorithms, you
     * should instead check the deadlock avoidance type and call the
     * appropriate function that you will complete in part 2.
     * @param transaction that is requesting the lock
     * @param tableName of requested table
     * @param lockType of requested lock
     */
    public void acquire(Transaction transaction, String tableName, LockType lockType)
            throws IllegalArgumentException {
        if (transaction.getStatus() == Transaction.Status.Waiting) {
            throw new IllegalArgumentException();
        }

        if (this.tableToTableLock.isEmpty() || !tableToTableLock.containsKey(tableName)) {
            this.tableToTableLock.put(tableName, new TableLock(lockType));
        }

        if (this.tableToTableLock.get(tableName).lockOwners.contains(transaction)
                && lockType == this.tableToTableLock.get(tableName).lockType) {
            throw new IllegalArgumentException();
        }
        //#1
        if (this.tableToTableLock.get(tableName).lockOwners.isEmpty()) {
            this.tableToTableLock.get(tableName).lockOwners.add(transaction);
            this.tableToTableLock.get(tableName).lockType = lockType;
            //#2
        } else {
            if (this.tableToTableLock.get(tableName).lockType == LockType.Exclusive) {
                if (lockType == LockType.Shared && this.tableToTableLock.get(tableName).lockOwners.contains(transaction)) {
                    throw new IllegalArgumentException();
                } else {
                    if (this.deadlockAvoidanceType == DeadlockAvoidanceType.None) {
                        this.tableToTableLock.get(tableName).requestersQueue.addLast(new Request(transaction, lockType));
                        transaction.sleep();
                    } else if (this.deadlockAvoidanceType == DeadlockAvoidanceType.WaitDie) {
                        waitDie(tableName, transaction, lockType);
                    } else if (this.deadlockAvoidanceType == DeadlockAvoidanceType.WoundWait) {
                        woundWait(tableName, transaction, lockType);
                    }
                }
            } else if (this.tableToTableLock.get(tableName).lockType == LockType.Shared) {
                //#3
                if (lockType == LockType.Shared) {
                    transaction.wake();
                    this.tableToTableLock.get(tableName).lockOwners.add(transaction);
                    this.tableToTableLock.get(tableName).lockType = lockType;
                    //#4
                } else if (lockType == LockType.Exclusive) {
                    if (this.tableToTableLock.get(tableName).lockOwners.contains(transaction)
                            && this.tableToTableLock.get(tableName).lockOwners.size() == 1
                            && this.tableToTableLock.get(tableName).lockType == LockType.Shared) {
                        if (lockType == LockType.Exclusive) {
                            transaction.wake();
                            this.tableToTableLock.get(tableName).lockOwners.clear();
                            this.tableToTableLock.get(tableName).lockOwners.add(transaction);
                            this.tableToTableLock.get(tableName).lockType = lockType;
                        }
                    } else {
                        if (this.deadlockAvoidanceType == DeadlockAvoidanceType.None) {
                            transaction.sleep();
                            this.tableToTableLock.get(tableName).requestersQueue.addFirst(new Request(transaction, lockType));
                        } else if (this.deadlockAvoidanceType == DeadlockAvoidanceType.WaitDie) {
                            waitDie(tableName, transaction, lockType);
                        } else if (this.deadlockAvoidanceType == DeadlockAvoidanceType.WoundWait) {
                            woundWait(tableName, transaction, lockType);
                        }
                    }
                }
                //#5
            }
        }
    }

    /**
     * This method will return true if the requested lock is compatible. See
     * spec provides compatibility conditions.
     * @param tableName of requested table
     * @param transaction requesting the lock
     * @param lockType of the requested lock
     * @return true if the lock being requested does not cause a conflict
     */
    private boolean compatible(String tableName, Transaction transaction, LockType lockType) {
        //TODO: HW5 Implement
        return false;
    }

    /**
     * Will release the lock and grant all mutually compatible transactions at
     * the head of the FIFO queue. See spec for more details.
     * @param transaction releasing lock
     * @param tableName of table being released
     */
    public void release(Transaction transaction, String tableName) throws IllegalArgumentException{
        //TODO: HW5 Implement
        if (transaction.getStatus() == Transaction.Status.Waiting) {
            throw new IllegalArgumentException();
        } else if (!this.tableToTableLock.containsKey(tableName)) {
            throw new IllegalArgumentException();
        }

        this.tableToTableLock.get(tableName).lockOwners.remove(transaction);

        //#1
        if (this.tableToTableLock.get(tableName).lockOwners.size() == 1
                && this.tableToTableLock.get(tableName).lockType == LockType.Shared) {
            List<Request> requestsCopy = new ArrayList<>(tableToTableLock.get(tableName).requestersQueue);
            for (Request request : requestsCopy) {
                if (this.tableToTableLock.get(tableName).lockOwners.contains(request.transaction)) {
                    if (request.lockType == LockType.Exclusive) {
                        this.tableToTableLock.get(tableName).lockOwners.clear();
                        request.transaction.wake();
                        this.tableToTableLock.get(tableName).lockOwners.add(request.transaction);
                        this.tableToTableLock.get(tableName).lockType = LockType.Exclusive;
                        this.tableToTableLock.get(tableName).requestersQueue.remove(request);
                    }
                }
            }
        }

        //#2
        if (this.tableToTableLock.get(tableName).lockType == LockType.Shared && !this.tableToTableLock.get(tableName).lockOwners.isEmpty()) {
            List<Request> requestsCopy = new ArrayList<>(tableToTableLock.get(tableName).requestersQueue);
            for (Request request : requestsCopy) {
                if (request.lockType == LockType.Shared) {
                    //grant
                    request.transaction.wake();
                    this.tableToTableLock.get(tableName).lockOwners.add(request.transaction);
                    this.tableToTableLock.get(tableName).requestersQueue.remove(request);
                }
            }
        }

        if (this.tableToTableLock.get(tableName).lockOwners.isEmpty() && !this.tableToTableLock.get(tableName).requestersQueue.isEmpty()) {
            //#3
            if (this.tableToTableLock.get(tableName).requestersQueue.getFirst().lockType == LockType.Shared) {
                List<Request> requestsCopy = new ArrayList<>(tableToTableLock.get(tableName).requestersQueue);
                for (Request request : requestsCopy) {
                    if (request.lockType == LockType.Shared) {
                        request.transaction.wake();
                        this.tableToTableLock.get(tableName).lockType = LockType.Shared;
                        this.tableToTableLock.get(tableName).lockOwners.add(request.transaction);
                        this.tableToTableLock.get(tableName).requestersQueue.remove(request);
                    }
                }

                //#4
            } else if (this.tableToTableLock.get(tableName).requestersQueue.getFirst().lockType == LockType.Exclusive) {
                Request request = this.tableToTableLock.get(tableName).requestersQueue.getFirst();
                this.tableToTableLock.get(tableName).lockType = LockType.Exclusive;
                this.tableToTableLock.get(tableName).lockOwners.clear();
                request.transaction.wake();
                this.tableToTableLock.get(tableName).lockOwners.add(request.transaction);
                this.tableToTableLock.get(tableName).requestersQueue.remove(request);
            }
            //#5
        } if (this.tableToTableLock.get(tableName).lockOwners.isEmpty()
                && this.tableToTableLock.get(tableName).requestersQueue.isEmpty()) {
            this.tableToTableLock.remove(tableName);
        }
    }

    /**
     * Will return true if the specified transaction holds a lock of type
     * lockType on the table tableName.
     * @param transaction holding lock
     * @param tableName of locked table
     * @param lockType of lock
     * @return true if the transaction holds lock
     */
    public boolean holds(Transaction transaction, String tableName, LockType lockType) {
        //TODO: HW5 Implement
        if (!this.tableToTableLock.containsKey(tableName)) {
            return false;
        } else {
            return this.tableToTableLock.get(tableName).lockType == lockType
                    && this.tableToTableLock.get(tableName).lockOwners.contains(transaction);
        }
    }

    /**
     * If transaction t1 requests an incompatible lock, t1 will abort if it has
     * a lower priority (higher timestamp) than all conflicting transactions.
     * If t1 has a higher priority, it will wait on the requesters queue.
     * @param tableName of locked table
     * @param transaction requesting lock
     * @param lockType of request
     */
    private void waitDie(String tableName, Transaction transaction, LockType lockType) {
        Iterator<Transaction> ownerIter = tableToTableLock.get(tableName).lockOwners.iterator();
        while (ownerIter.hasNext()) {
            Transaction currTransaction = ownerIter.next();
            if (transaction.getTimestamp() > currTransaction.getTimestamp()) {
                transaction.abort();
                return;
            }
        }
        transaction.sleep();
        tableToTableLock.get(tableName).requestersQueue.addLast(new Request(transaction, lockType));
    }

    /**
     * If transaction t1 requests an incompatible lock, t1 will wait if it has
     * a lower priority (higher timestamp) than conflicting transactions. If t1
     * has a higher priority than every conflicting transaction, it will abort
     * all the lock holders and acquire the lock.
     * @param tableName of locked table
     * @param transaction requesting lock
     * @param lockType of request
     */
    private void woundWait(String tableName, Transaction transaction, LockType lockType) {
        List<Transaction> ownerList = new ArrayList<>(tableToTableLock.get(tableName).lockOwners);
        Transaction biggest = ownerList.get(0);
        for (Transaction owner : ownerList) {
            if (owner.getTimestamp() > biggest.getTimestamp()) {
                biggest = owner;
            }
        }

        if (transaction.getTimestamp() < biggest.getTimestamp() && !tableToTableLock.get(tableName).lockOwners.contains(transaction)) {
            List<Transaction> owners = new ArrayList<>(tableToTableLock.get(tableName).lockOwners);
            for (Transaction owner : owners) {
                owner.abort();
                tableToTableLock.get(tableName).lockOwners.clear();
                tableToTableLock.get(tableName).lockOwners.add(transaction);
                tableToTableLock.get(tableName).lockType = lockType;
                List<Request> requests = new ArrayList<>(tableToTableLock.get(tableName).requestersQueue);
                for (Request request : requests) {
                    if (request.transaction == owner) {
                        tableToTableLock.get(tableName).requestersQueue.remove(request);
                    }
                }
            }
        } else {
            transaction.sleep();
            tableToTableLock.get(tableName).requestersQueue.addLast(new Request(transaction, lockType));
        }
    }

    /**
     * Contains all information about the lock for a specific table. This
     * information includes lock type, lock owner(s), and lock requestor(s).
     */
    private class TableLock {
        private LockType lockType;
        private HashSet<Transaction> lockOwners;
        private LinkedList<Request> requestersQueue;

        public TableLock(LockType lockType) {
            this.lockType = lockType;
            this.lockOwners = new HashSet<Transaction>();
            this.requestersQueue = new LinkedList<Request>();
        }

    }

    /**
     * Used to create request objects containing the transaction and lock type.
     * These objects will be added to the requestor queue for a specific table
     * lock.
     */
    private class Request {
        private Transaction transaction;
        private LockType lockType;

        public Request(Transaction transaction, LockType lockType) {
            this.transaction = transaction;
            this.lockType = lockType;
        }
    }
}
