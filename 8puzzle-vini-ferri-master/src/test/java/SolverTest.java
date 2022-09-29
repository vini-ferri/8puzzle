import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SolverTest {
    private Solver solver;
    private Solver solver4x4;
    private List<Board> solution;

    @BeforeEach
    void setUp() {
        int[][] tiles = {
                {1, 3, 0},
                {4, 2, 5},
                {7, 8, 6}
        };
        int[][] tiles4 = {
                {1, 2, 3, 4},
                {5, 0, 6, 8},
                {9, 10, 7, 11},
                {13, 14, 15, 12}
        };

        Board board = new Board(tiles);
        Board board4x4 = new Board(tiles4);

        int[][] move1 = {
                {1, 0, 3}, {4, 2, 5}, {7, 8, 6}
        };
        int[][] move2 = {
                {1, 2, 3}, {4, 0, 5}, {7, 8, 6}
        };
        int[][] move3 = {
                {1, 2, 3}, {4, 5, 0}, {7, 8, 6}
        };
        int[][] move4 = {
                {1, 2, 3}, {4, 5, 6}, {7, 8, 0}
        };

        solver = new Solver(board);
        solver4x4 = new Solver(board4x4);
        Board[] temp = {new Board(tiles), new Board(move1), new Board(move2), new Board(move3), new Board(move4)};
        solution = Arrays.asList(temp);
    }

    @Test
    @DisplayName("moves deve retornar corretamente o número de movimentos")
    void moves() {
        int expected = 4;
        assertEquals(expected, solver.moves());
    }
    @Test
    @DisplayName("moves deve retornar corretamente o número de movimentos em um tabuleiro 4x4")
    void moves4x4() {
        int expected = 4;
        assertEquals(expected, solver4x4.moves());
    }

    @Test
    @DisplayName("solution deve corretamente retornar a solução viável")
    void solution() {
        assertEquals(solution, solver.solution());
    }
}