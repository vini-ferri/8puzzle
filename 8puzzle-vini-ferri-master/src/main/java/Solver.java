import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Solver {
    private int moves;
    private final Queue<Board> solution = new ArrayDeque<>();

        // encontra a solução para o tabuleiro inicial (usando o algoritmo A*)
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException();

        PriorityQueue<SearchNode> pq = new PriorityQueue<>(new SearchNodeComparator());

        pq.add(new SearchNode(initial, null));

        boolean gotSolution = false;
        while (!gotSolution) {
            SearchNode removedNode = pq.poll();
            if (Objects.requireNonNull(removedNode).board.isGoal()) {
                moves = removedNode.moves;
                do {
                    solution.add(removedNode.board);
                    removedNode = removedNode.previous;
                } while (removedNode != null);
                gotSolution = true;
            } else {
                for (Board neighbor : removedNode.board.neighbors()) {
                    if ((removedNode.previous == null) || !neighbor.equals(removedNode.previous.board)) {
                        pq.add(new SearchNode(neighbor, removedNode));
                    }
                }
            }
        }
    }

        // min número de movimentos para resolver o tabuleiro inicial
    public int moves() {
        return moves;
    }

        // sequência de tabuleiros na solução mais curta
    public Iterable<Board> solution() {
        Stack<Board> copySolution = new Stack<>();

        for (Board board : solution) {
            copySolution.push(board);
        }

        return new RevertStack(copySolution).revert();
    }

    private static int[][] loadFileIntoArray(String fileName) throws FileNotFoundException {
        Scanner in = new Scanner(new File(fileName));
        int n = in.nextInt();
        int[][] tiles = new int[n][n];

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                tiles[x][y] = in.nextInt();
            }
        }

        in.close();
        return tiles;
    }

    public static void main(String[] args) {
        try {
            int[][] tiles = loadFileIntoArray(args[0]);
            Board initial = new Board(tiles);

            if (initial.isSolvable()) {
                Solver solver = new Solver(initial);
                System.out.println("Mínimo número de movimentos = " + solver.moves());
                for (Board board : solver.solution()) {
                    System.out.println(board);
                }
            } else {
                System.out.println("Puzzle irresolvível!");
                System.out.println(initial);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static class SearchNode {
        private final Board board;
        private final int moves;
        private final int manhattan;
        private final SearchNode previous;

        SearchNode(Board board, SearchNode previous) {
            if (board == null)
                throw new IllegalArgumentException();

            this.board = board;
            this.previous = previous;

            if (previous == null) {
                moves = 0;
            } else {
                moves = previous.moves + 1;
            }

            manhattan = board.manhattan();
        }
    }

    private static class SearchNodeComparator implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode o1, SearchNode o2) {
            if (o1.manhattan + o1.moves == o2.manhattan + o2.moves) {
                return o1.manhattan - o2.manhattan;
            } else {
                return o1.manhattan + o1.moves - o2.manhattan - o2.moves;
            }
        }
    }

    private record RevertStack(Stack<Board> stack) {

        public Stack<Board> revert() {
                Stack<Board> revertedStack = new Stack<>();

                while (!stack.empty()) {
                    revertedStack.push(stack.pop());
                }

                return revertedStack;
            }
        }
}