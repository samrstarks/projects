package hw4.puzzle;

public class Board {
    private int N;
    private int moves;
    private final int[][] board;
    private final int[][] goalBoard;

    public Board(int[][] tiles) {
        N = tiles.length;
        moves = 0;
        board = new int[N][N];
        goalBoard = new int[N][N];

        //setting up input board:
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board[i][j] = tiles[i][j];
            }
        }

        //setting up goal board:
        int count = 1;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                goalBoard[i][j] = (N * i) + j;
            }
        }
    }

    public int tileAt(int i, int j) {
        return board[i][j];
    }

    public int size() {
        return N;
    }

    public int moves() {
        return moves;
    }

    public int hamming() {
        int count = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] != goalBoard[i][j] && board[i][j] != 0) {
                    count++;
                }
            }
        }
        return count + moves;
    }

    public int manhattan() {
        int count = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (!isSpace(board[i][j])) {
                    int distance = Math.abs(i - row(board[i][j])) 
                        + Math.abs(j - col(board[i][j]));
                    count += distance;
                }
            }
        }
        return count + moves;
    }

    public boolean isSpace(int square) {
        return square == 0;
    }

    public int row(int square) {
        return (square - 1) / N;
    }

    public int col(int square) {
        return (square - 1) % N;
    }

    public int getIndex(int i, int j) {
        return (N * i) + j;
    }

    public boolean isGoal() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] != 0) {
                    if (board[i][j] != (i * N + j + 1)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object y) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (((Board) y).board[i][j] != board[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    /** Returns the string representation of the board. */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int M = N;
        s.append(M + "\n");
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

}
