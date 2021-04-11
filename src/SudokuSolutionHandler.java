import java.util.List;

public class SudokuSolutionHandler implements SolutionHandler {
    @Override
    public void handleSolution(List<DancingNode> solution) {
        int N = (int) Math.sqrt(solution.size());
        int[][] sudokuSolution = new int[N][N];
        for (DancingNode node : solution) {
            int min = Integer.parseInt(node.C.name);
            int secondMin = Integer.MAX_VALUE;
            int row, column, number;
            for (DancingNode temp = node.R; temp != node; temp = temp.R) {
                int curr = Integer.parseInt(temp.C.name);
                if (curr < min) {
                    secondMin = min;
                    min = curr;
                } else if (curr < secondMin && curr != min)
                    secondMin = curr;
            }

            row = min / N;
            column = min % N;
            number = (secondMin % N) + 1;

            sudokuSolution[row][column] = number;
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(sudokuSolution[i][j] + " ");
            }
            System.out.println();
        }
    }
}
