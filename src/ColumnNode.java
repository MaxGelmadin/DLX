public class ColumnNode extends DancingNode {

    protected int size;
    protected String name;

    protected ColumnNode(String name) {
        super();
        this.name = name;
        size = 0;
        C = this;
    }

    /**
     * Covers (AKA removes) given column 'c'.
     */
    protected void coverColumn() {
        this.removeLR();
        for (DancingNode i = this.D; i != C; i = i.D) {
            for (DancingNode j = i.R; j != i; j = j.R) {
                j.removeUD();
                j.C.size--;
            }
        }
    }

    /**
     * Uncovers column 'c' back to the list.
     * 'c' of course must not be in the list before activating this function.
     */
    protected void uncoverColumn() {
        for (DancingNode i = this.U; i != this; i = i.U) {
            for (DancingNode j = i.L; j != i; j = j.L) {
                j.C.size++;
                j.undoRmUD();
            }
        }
        this.undoRmLR();
    }
}

