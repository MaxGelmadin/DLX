import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This is an implementation of Donald Knuth's Algorithm-X which can be used to solve
 * the 'Exact Cover Problem' or different problems that can be reduced to this problem.
 * @see <a href="https://www.ocf.berkeley.edu/~jchu/publicportal/sudoku/0011047.pdf">https://www.ocf.berkeley.edu/~jchu/publicportal/sudoku/0011047.pdf</a>
 *
 */

public class DancingList {

    // Fields
    private static int solutionNum;

    private ColumnNode head;
    private LinkedList<DancingNode> solution;
    private SolutionHandler solutionHandler;


    // Constructor
    public DancingList(int[][] grind, SolutionHandler h) {
        solutionNum = 0;
        solutionHandler = h;
        final int COLS = grind[0].length;
        ArrayList<ColumnNode> colList = new ArrayList<>();
        solution = new LinkedList<>();
        head = new ColumnNode("head");

        // Creating column nodes
        ColumnNode temp = head;
        for (int i = 0; i < COLS; i++) {
            ColumnNode toAdd = new ColumnNode(Integer.toString(i));
            colList.add(toAdd);
            temp.hookRight(toAdd);
            temp = (ColumnNode) temp.R;
        }
        temp.hookRight(head);

        for (int[] ints : grind) {
            DancingNode prev = null;
            DancingNode first = null;
            for (int j = 0; j < COLS; j++) {
                if (ints[j] == 1) {
                    DancingNode toAdd = new DancingNode();
                    ColumnNode col = colList.get(j);
                    toAdd.C = col;
                    col.size++;
                    if (prev == null)
                        prev = toAdd;
                    if (first == null)                                              // Remember the first node in order to hook him with the last one
                        first = toAdd;
                    col.U.hookDown(toAdd);
                    toAdd.hookDown(col);
                    prev.hookRight(toAdd);
                    prev = prev.R;
                }
            }
            if (first != null)                                                      // connecting between last (prev) added node with the first added node
                prev.hookRight(first);
        }
    }

    /**
     * Chooses column to cover.
     * The column with minimum number of 1's will be chosen and returned.
     * If the all columns are covered, the head of the list will be returned instead.
     *
     * @return column
     */
    private ColumnNode chooseColumn() {
        ColumnNode ret = head;
        int s = Integer.MAX_VALUE;
        for (ColumnNode i = (ColumnNode) head.R; i != head; i = (ColumnNode) i.R) {
            if (i.size < s) {
                s = i.size;
                ret = i;
            }
        }
        return ret;
    }

    // This one finds the solutions in the given matrix.
    private void search(int k) {
        if (head.R == head) {
            solutionHandler.handleSolutionList(solution);                           // Solution is found
        }
        else {
            ColumnNode currColumn = chooseColumn();                                 // Choose next column to cover. The column with minimum number of 1's is chosen
            currColumn.coverColumn();
            for (DancingNode r = currColumn.D; r != currColumn; r = r.D) {
                solution.add(r);
                for (DancingNode j = r.R; j != r; j = j.R) {
                    j.C.coverColumn();
                }
                search(k + 1);
                r = solution.removeLast();
                currColumn = r.C;
                for (DancingNode j = r.L; j != r; j = j.L) {
                    j.C.uncoverColumn();
                }
            }
            currColumn.uncoverColumn();
        }
    }

    public void run() {
        search(0);
    }

    public static int getSolutionNum() {
        return solutionNum;
    }

    public static void increment() {
        solutionNum++;
    }
}
