import java.util.Stack;

public class Board {
    private final int n;
    private final int[][] tiles;
    private int hamming;
    private int manhattan;

        // constrói um tabuleiro a partir de um array de quadros N-por-N
        // (onde tiles[x][y] = quadro na linha x, coluna y)
    public Board(int[][] tiles) {
        if (tiles == null)
            throw new IllegalArgumentException();
        if (tiles.length != tiles[0].length)
            throw new IllegalArgumentException();

        n = tiles[0].length;
        this.tiles = new int[n][n];

        System.arraycopy(tiles, 0, this.tiles, 0, n);

        hamming = calculateHamming();
        manhattan = calculateManhattan();
    }

        // retorna o quadro na linha x, coluna y (ou 0 se vazio)
    public int tileAt(int x, int y) {
        if ((x < 0 && x < size()) || (y < 0 && y < size())) {
            throw new IndexOutOfBoundsException();
        }

        return tiles[x][y];
    }

        // o tamanho N do tabuleiro
    public int size() {
        return n;
    }

        // número de quadros fora do lugar
    public int hamming() {
        return hamming;
    }

        // soma das distâncias de Manhattan entre os quadros e o objetivo final
    public int manhattan() {
        return manhattan;
    }

        // este tabuleiro é o tabuleiro objetivo?
    public boolean isGoal() {
        for (int x = 0; x < size(); x++) {
            for (int y = 0; y < size(); y++) {
                if (tileAt(x, y) != (y + 1) + (x * size())) {
                    if (!((x == size() - 1) && (y == size() - 1))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

        // este tabuleiro é resolvível?
    public boolean isSolvable() {
        int[] linearPuzzle;
        int k = 0;

        linearPuzzle = new int[size() * size()];

        for (int x = 0; x < size(); x++) {
            for (int y = 0; y < size(); y++) {
                linearPuzzle[k++] = tileAt(x, y);
            }
        }

        int invCount = getInversionCount(linearPuzzle);

        if (size() == 4) {
            return (invCount % 2 != 0);
        }

        return (invCount % 2 == 0);
    }

            // este tabuleiro é igual ao tabuleiro y?
    public boolean equals(Object y) {
        if (this == y) {
            return true;
        }

        if (y == null) {
            return false;
        }

        if (this.getClass() != y.getClass()) {
            return false;
        }

        Board that = (Board) y;
        if (size() != that.size()) {
            return false;
        }

        for (int x = 0; x < size(); x++) {
            for (int y = 0; y < size(); y++) {
                if (tileAt(x, y) != that.tileAt(x, y)) {
                    if (!((x == size() - 1) && (y == size() - 1))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

        // todos os tabuleiros vizinhos
    public Iterable<Board> neighbors() {
        int emptyI = 0;
        int emptyJ = 0;
        boolean gotEmpty = false;

        for (int x = 0; x < size(); x++) {
            for (int y = 0; y < size(); y++) {
                if (tileAt(x, y) == 0) {
                    emptyI = x;
                    emptyJ = y;
                    gotEmpty = true;
                    break;
                }
            }

            if (gotEmpty) {
                break;
            }
        }

        Stack<Board> neighbors = new Stack<>();
        int[][] increments = { { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, 0 } }; // UP, DOWN, RIGHT, LEFT

        for (int[] incr : increments) {
            if ((emptyI + incr[0] < size()) && (emptyI + incr[0] >= 0)) {
                if ((emptyJ + incr[1] >= 0) && (emptyJ + incr[1] < size())) {
                    int[][] neighborTiles = new int[size()][size()];

                    for (int x = 0; x < size(); x++) {
                        for (int y = 0; y < size(); y++) {
                            neighborTiles[x][y] = tileAt(x, y);
                        }
                    }

                    neighborTiles[emptyI][emptyJ] = tiles[emptyI + incr[0]][emptyJ + incr[1]];
                    neighborTiles[emptyI + incr[0]][emptyJ + incr[1]] = 0;
                    neighbors.push((new Board(neighborTiles)));
                }
            }
        }

        return neighbors;
    }


    public String toString() {
        StringBuilder outStr = new StringBuilder(Integer.toString(size())).append("\n");

        for (int x = 0; x < size(); x++) {
            for (int y = 0; y < size(); y++) {
                outStr.append(" ").append(tileAt(x, y));
            }
            outStr.append("\n");
        }

        return outStr.toString();
    }

    private int calculateHamming() {
        int hammingLocal = 0;
        for (int x = 0; x < size(); x++) {
            for (int y = 0; y < size(); y++) {
                if (tileAt(x, y) != (y + 1) + (x * size())) {
                    hammingLocal++;
                }
            }
        }
        hammingLocal -= 1;
        return hammingLocal;
    }

    private int calculateManhattan() {
        int manhattanLocal = 0;
        for (int x = 0; x < size(); x++) {
            for (int y = 0; y < size(); y++) {
                if (tileAt(x, y) != 0) {
                    int iOrig = (tileAt(x, y) - 1) / size();
                    int jOrig = (tileAt(x, y) - 1) % size();
                    manhattanLocal += Math.abs(iOrig - x) + Math.abs(jOrig - y);
                }
            }
        }
        return manhattanLocal;
    }

    private int getInversionCount(int[] arr) {
        int inversionCount = 0;

        for (int x = 0; x < 9; x++) {
            for (int y = x + 1; y < 9; y++) {
                if (arr[x] > 0 && arr[y] > 0 && arr[x] > arr[y]) {
                    inversionCount++;
                }
            }
        }

        return inversionCount;
    }
}
