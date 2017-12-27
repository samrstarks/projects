package hw4.puzzle;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import java.util.ArrayList;

public class Solver {
    private MinPQ<Node> movesPQ;
    private Node prevMove;
    private int movesCount;
    private Node finalBoard;
    private int moves;
    private ArrayList<Board> movesList = new ArrayList<Board>();

    public Solver(Board initial) {
        movesPQ = new MinPQ<Node>();
        Node ogBoard = new Node(initial, null);
        movesPQ.insert(ogBoard);
        while (!movesPQ.min().curBoard.isGoal()) {
            Node min = movesPQ.delMin();
            for (Board neighbor : BoardUtils.neighbors(min.curBoard)) {
                if (min.prevBoard == null || !neighbor.equals(min.prevBoard.curBoard)) {
                    Node nextBoard = new Node(neighbor, min);
                    movesPQ.insert(nextBoard);
                }
            }
        }
        finalBoard = movesPQ.delMin();
        moves = finalBoard.numMoves;
        int count = finalBoard.numMoves;
        while (finalBoard.prevBoard != null) {
            movesList.add(finalBoard.curBoard);
            finalBoard = finalBoard.prevBoard;
        }
        movesList.add(finalBoard.curBoard);
    }

    public int moves() {
        return moves;
    }

    public Iterable<Board> solution() {
        return reverse(movesList);
    }

    public ArrayList<Board> reverse(ArrayList<Board> list) {
        for (int i = 0, j = list.size() - 1; i < j; i++) {
            list.add(i, list.remove(j));
        }
        return list;
    }

    public class Node implements Comparable<Node> {
        private Board curBoard;
        private Node prevBoard;
        private int numMoves = 0;

        /*private Node(Board board) {
            this.curBoard = board;
        }*/

        private Node(Board board, Node prev) {
            if (prev != null) {
                this.curBoard = board;
                this.prevBoard = prev;
                this.numMoves = prev.numMoves + 1;
            } else {
                this.curBoard = board;
                this.prevBoard = prev;
                this.numMoves = 0;
            }
        }

        @Override
        public int compareTo(Node board) {
            return (this.curBoard.manhattan() 
                - board.curBoard.manhattan()) + (this.numMoves - board.numMoves);
        }
    }


    // DO NOT MODIFY MAIN METHOD
    public static void main(String[] args) {
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] tiles = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        Board initial = new Board(tiles);
        Solver solver = new Solver(initial);
        StdOut.println("Minimum number of moves = " + solver.moves());
        for (Board board : solver.solution()) {
            StdOut.println(board);
        }
    }
}
