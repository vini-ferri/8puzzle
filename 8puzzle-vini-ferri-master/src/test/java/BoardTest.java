import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;
    private Board secondBoard;
    private int[][] tiles;
    private Board goalBoard;
    private Board unsolvableBoard;
    private Board board4;
    @BeforeEach
    void setUp() {
        int[][] unsolvable = {
                {1, 2, 3},
                {4, 5, 6},
                {8, 7, 0}
        };

        int[][] goal = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0}
        };

        int[][] tiles = {
                {0, 1, 3},
                {4, 2, 5},
                {7, 8, 6}
        };

        int[][] tiles4 = {
                {1, 2, 3, 4},
                {5, 0, 6, 8},
                {9, 10, 7, 11},
                {13, 14, 15, 12}
        };

        this.tiles = tiles;
        board = new Board(tiles);
        board4 = new Board(tiles4);
        secondBoard = new Board(tiles);
        goalBoard = new Board(goal);
        unsolvableBoard = new Board(unsolvable);
    }

    @Test
    @DisplayName("tileAt deve retornar o valor correto.")
    void tileAtShouldReturn() {
        int expected = 3;
        int actual = board.tileAt(0, 2);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("tileAt deve lançar uma exceção para índices de linha negativos.")
    void tileAtShouldThrowInvalidUnderRowValue() {
        IndexOutOfBoundsException thrown = assertThrows(IndexOutOfBoundsException.class, () -> board.tileAt(-1, 2), "Deveria lançar uma IndexOutOfBoundsException para os linha negativa");
    }

    @Test
    @DisplayName("tileAt deve lançar uma exceção para índices de linha maiores que o tamanho do board.")
    void tileAtShouldThrowInvalidOverRowValue() {
        int size = board.size();
        IndexOutOfBoundsException thrown = assertThrows(IndexOutOfBoundsException.class, () -> board.tileAt(size, 2), "Deveria lançar uma IndexOutOfBoundsException para linha maior que o tamanho do board");
    }

    @Test
    @DisplayName("tileAt deve lançar uma exceção para índices de coluna negativo.")
    void tileAtShouldThrowUnderColValue() {
        IndexOutOfBoundsException thrown = assertThrows(IndexOutOfBoundsException.class, () -> board.tileAt(2, -1), "Deveria lançar uma IndexOutOfBoundsException para coluna negativa");
    }

    @Test
    @DisplayName("tileAt deve lançar uma exceção para índices de coluna maiores que o tamanho do board.")
    void tileAtShouldThrowInvalidOverColValue() {
        int size = board.size();
        IndexOutOfBoundsException thrown = assertThrows(IndexOutOfBoundsException.class, () -> board.tileAt(1, size), "Deveria lançar uma IndexOutOfBoundsException para coluna maior que o tamanho do board");
    }

    @Test
    @DisplayName("size deve retornar corretamente o tamanho do board")
    void size() {
        int expected = tiles.length;
        int actual = board.size();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("hamming deve ser calculado corretamente")
    void hamming() {
        int expected = 4;
        assertEquals(expected, board.hamming());
    }

    @Test
    @DisplayName("hamming deve ser calculado corretamente para um tabuleiro 4x4")
    void hamming4x4() {
        int expected = 4;
        assertEquals(expected, board4.hamming());
    }

    @Test
    @DisplayName("manhattan deve ser calculado corretamente")
    void manhattan() {
        int expected = 4;
        assertEquals(expected, board.manhattan());
    }

    @Test
    @DisplayName("manhattan deve ser calculado corretamente para um tabuleiro 4x4")
    void manhattan4x4() {
        int expected = 4;
        assertEquals(expected, board4.manhattan());
    }

    @Test
    @DisplayName("isGoal deve ser true para um board goal")
    void isGoalTrue() {
        assertTrue(goalBoard.isGoal());
    }

    @Test
    @DisplayName("isGoal deve ser false para um board não goal")
    void isGoalFalse() {
        assertFalse(board.isGoal());
    }

    @Test
    @DisplayName("isSolvable deve ser true para um board resolvível")
    void isSolvableTrue() {
        assertTrue(board.isSolvable());
    }

    @Test
    @DisplayName("isSolvable deve ser true para um board inresolvível")
    void isSolvableFalse() {
        assertFalse(unsolvableBoard.isSolvable());
    }

    @Test
    @DisplayName("equals deve corretamente indicar que dois boards são iguais")
    void testEqualsTrue() {
        assertEquals(board, secondBoard);
    }

    @Test
    @DisplayName("equals deve corretamente indicar que dois boards são diferentes")
    void testEqualsFalse() {
        assertNotEquals(board, goalBoard);
    }

    @Test
    @DisplayName("neighbors deve retornar os tabuleiros vizinhos corretamente")
    void neighbors() {
        int[][] neighbors1 = {
                {1, 0, 3}, {4, 2, 5}, {7, 8, 6}
        };
        int[][] neighbors2 = {
                {4, 1, 3}, {0, 2, 5}, {7, 8, 6}
        };
        List<Board> expected = new ArrayList<>();
        expected.add(new Board(neighbors2));
        expected.add(new Board(neighbors1));

        List<Board> actual = new ArrayList<>();
        board.neighbors().forEach(actual::add);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("toString deve corretamente retornar uma representação do board")
    void testToString() {
        String expected = "3\n 0  1  3 \n 4  2  5 \n 7  8  6 \n";
        assertEquals(expected, board.toString());
    }
}