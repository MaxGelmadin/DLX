public class DancingNode {

    public static int updates = 0;
    protected DancingNode L, R, U, D;
    protected ColumnNode C;

    // Constructors
    protected DancingNode() {
        L = R = U = D = this;
    }

    protected DancingNode(ColumnNode C) {
        this();
        this.C = C;
    }

    protected void removeLR() {
        this.R.L = this.L;
        this.L.R = this.R;
        updates++;
    }

    protected void removeUD() {
        this.D.U = this.U;
        this.U.D = this.D;
        updates++;
    }

    protected void undoRmLR() {
        this.R.L = this;
        this.L.R = this;
        updates++;
    }

    protected void undoRmUD() {
        this.D.U = this;
        this.U.D = this;
        updates++;
    }

    protected void hookRight(DancingNode other) {
        this.R = other;
        other.L = this;
    }

    protected void hookDown(DancingNode other) {
        this.D = other;
        other.U = this;
    }
}
