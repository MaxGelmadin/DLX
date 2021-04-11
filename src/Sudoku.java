import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Sudoku {

    private final int N;
    private final int MAX_VAL;
    private final int LIMIT;
    private int DELTA;
    private int[][] sudokuGrind;
    private byte[][] exactCoverGrind;

    public Sudoku(int N, String level) {
        this.N = N;
        MAX_VAL = N * N;
        LIMIT = MAX_VAL * MAX_VAL;
        DELTA =  0;

        sudokuGrind = new int[N * N][N * N];
        BufferedReader buff;
        try {
            buff = new BufferedReader(new FileReader("levels/" + level));
            parseBoard(buff);
        } catch (FileNotFoundException ex) {
            System.out.println("File " + level + " was not found. Terminating...");
            System.exit(0);
        }
        exactCoverGrind = createExactCoverBoard();
        finalizeGrind();
    }

    /**
     * Travers over the given parsed Sudoku board. If cell contains '0', we do nothing.
     * Otherwise, this cell contains a number in [1, N^2], thus we must force the DLX to include this column in
     * the final solution.
     * <p>
     *     In order to do this we use {@getIndex(int; int; int)} to find what is the position of the number.
     *     Then, we fill with zero's all N^2 lines in the so the column that contains the specific number will be
     *     the only column that contains 1.
     *     This way, we will force the DLX to choose this number.
     * </p>
     */
    private void finalizeGrind() {
        for (int i = 0; i < sudokuGrind.length; i++) {
            for (int j = 0; j < sudokuGrind[0].length; j++) {
                int number = sudokuGrind[i][j];
                if (number != 0) {
                    //int coverGrindIndex = getIndex(i, j, number);
                    for (int k = 1; k <= MAX_VAL; k++) {
                        if (k != number)
                            Arrays.fill(exactCoverGrind[getIndex(i, j, k)], (byte) 0);
                    }
                }
            }
        }
    }


    private void parseBoard(BufferedReader buff) {
            try {
                int c;
                int position = 0;
                while ((c = buff.read()) != -1) {
                    if (c == '\r' | c == '\n') {
                        continue;
                    }
                    sudokuGrind[position / MAX_VAL][position % MAX_VAL] = (byte) (c == '.' ? 0 : c - '0');
                    position++;
                }
            } catch (IOException ex) {
                System.out.println("IO error occurred during file read. Terminating...");
                try {
                    buff.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        }

    private int getIndex(int row, int column, int num) {
        return row * MAX_VAL * MAX_VAL + column * MAX_VAL + (num - 1);
    }

    /**
     * Here we build the Sudoku constraints.
     * Better visualized here: <a href="https://www.stolaf.edu//people/hansonr/sudoku/exactcovermatrix.htm">https://www.stolaf.edu//people/hansonr/sudoku/exactcovermatrix.htm</a>
     *
     */
   private byte[][] createExactCoverBoard() {
        byte[][] coverBoard = new byte
                [N * N * (MAX_VAL * MAX_VAL)]
                [N * N * MAX_VAL * 4];


        // Cell constraints. Each cell contains only one integer in [1, MAX_VAL]
        int offsetR = 0, offsetC = 0;
        for (int j = DELTA; j < LIMIT + DELTA; j++) {
            for (int i = 0; i < MAX_VAL; i++) {
                coverBoard[i + offsetR][j] = 1;
            }
            offsetR += MAX_VAL;
        }

        offsetR = 0;
        DELTA = DELTA + LIMIT;


        // Row constraints. An integer must be present exactly one time in a row.
        for (int i = 0; i < coverBoard.length; i += MAX_VAL) {
            offsetC = ((i % LIMIT == 0) && i != 0) ?  (offsetC + MAX_VAL) : offsetC;
            for (int j = DELTA; j < DELTA + MAX_VAL; j++) {
                coverBoard[i + offsetR][j + offsetC] = 1;
                offsetR++;
            }
            offsetR = 0;
        }

        DELTA = DELTA + LIMIT;


        // Column constraints. An integer must be present exactly one time in a row.
        for (int i = 0; i < coverBoard.length; i += LIMIT) {
            offsetR = 0;
            for (int j = DELTA; j < DELTA + LIMIT; j++) {
                coverBoard[i + offsetR][j] = 1;
                offsetR++;
            }
        }

        DELTA = DELTA + LIMIT;


        // Box constraints. Every integer in [0, MAX_VAL] must be present in a box of size N * N.
        // It's pretty weird - I looked for a pattern here, and the bellow solution is what I came up with.
        int i = 0, j = DELTA, counter = 0;
        while (i < coverBoard.length & j < coverBoard[0].length) {
            coverBoard[i][j] = 1;
            i++;
            j++;
            if (i % (MAX_VAL * MAX_VAL) == 0) {
                counter++;
                if (counter % N != 0)
                    j -= N * MAX_VAL;
            }
            else if (j % MAX_VAL == 0) {
                    if (i % (MAX_VAL * N) != 0)
                        j -= MAX_VAL;
                }
        }

        return coverBoard;
    }

    public void run() {
        //DancingList lst = new DancingList(exactCoverGrind, new PrintSolutionHandler());
        DancingList lst = new DancingList(exactCoverGrind, new SudokuSolutionHandler());
        lst.run();
    }
}
