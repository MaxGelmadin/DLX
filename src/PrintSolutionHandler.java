import java.util.List;

public class PrintSolutionHandler implements SolutionHandler {
    @Override
    public void handleSolution(List<DancingNode> solution) {
        DancingList.increment();
        System.out.println("Printing solution number: " + DancingList.getSolutionNum());
        printSolution(solution);
        System.out.println("-------------------------------------------");
    }

    private void printSolution(List<DancingNode> solution) {
        for (DancingNode node : solution) {
            StringBuilder sol = new StringBuilder(node.C.name);
            DancingNode temp = node.R;
            while (temp != node) {
                sol.append(" ").append(temp.C.name);
                temp = temp.R;
            }
            System.out.println(sol);
        }
    }
}
