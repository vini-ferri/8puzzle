[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-c66648af7eb3fe8bc4f294546bfd86ef473780cde1dea487d3c4ff354943c9ae.svg)](https://classroom.github.com/online_ide?assignment_repo_id=8477294&assignment_repo_type=AssignmentRepo)
# 8-Puzzle

Escreva um programa que solucione o problema do *8-puzzle* (e suas generalizações naturais) utilizando o algoritmo de busca A*.

## O Problema

O problema [8-puzzle](http://en.wikipedia.org/wiki/Fifteen_puzzle) é um *puzzle* inventado e popularizado por Noyes Palmer Chapman nos anos 1870. É jogado em um grid de 3x3 quadrados, com 8 quadrados marcados com os números 1 a 8 e um quadrado vazio. Seu objetivo é reorganizar os quadrados de forma que eles fiquem ordenados, usando o menor número de movimentos.

Você pode arrastar os quadrados horizontalmente ou verticalmente, para a posição do quadrado em branco.

A seguir você pode ver uma sequência de movimentos legais, começando em um *tabuleiro inicial* (esquerda) até o *tabuleiro objetivo* (direita).

```
   1  3        1     3        1  2  3        1  2  3        1  2  3
4  2  5   =>   4  2  5   =>   4     5   =>   4  5      =>   4  5  6
7  8  6        7  8  6        7  8  6        7  8  6        7  8

inicial      1 esquerda       2 cima       5 esquerda       objetivo
```

## Busca *best-fit*

Agora, descrevemos a solução para o problema, que ilustra uma metodologia de IA chamada [algoritmo de busca A*](http://en.wikipedia.org/wiki/A*_search_algorithm). Definimos um *nó de busca* do jogo, como um tabuleiro (*board*), o número de movimentos feitos para chegar até esse tabuleiro e o nó de busca anterior.

Primeiro, inserimos o nó de busca inicial (o tabuleiro inicial, 0 movimentos e um nó de busca anterior *null*) em uma fila de prioridade (*priority queue*). Então, removemos da fila de prioridade o nó de busca com a menor prioridade e inserimos nessa fila todos os nós vizinhos (aqueles que podem ser atingidos a partir de um movimento deste nó que foi desenfileirado).

Repete-se este procedimento até que o nó de busca desenfileirado corresponda ao tabuleiro objetivo.

O sucesso desta abordagem baseia-se na escolha de uma *função de prioridade* para o nó de busca. Consideramos duas funções de prioridade:

### Função de prioridade de Hamming

O número de casas na posição incorreta, somados ao número de movimentos feitos até aqui para se chegar ao nó de busca atual. Intuitivamente, um nó de busca com um pequeno número de casas na posição incorreta está próximo do objetivo, e preferimos um nó que foi atingido usando-se um pequeno número de movimentos.

### Função de prioridade de Manhattan

A soma das distâncias de *Manhattan* (soma da distância vertical e horizontal) das casas para as suas posições objetivas, somadas ao número de movimentos feitos até aqui para se chegar ao nó de busca atual.

Por exemplo, as prioridades de *Hamming* e *Manhattan* do nó de busca inicial abaixo são de 5 e 10, respectivamente.

```
 8  1  3        1  2  3     1  2  3  4  5  6  7  8    1  2  3  4  5  6  7  8
 4     2        4  5  6     ----------------------    ----------------------
 7  6  5        7  8        1  1  0  0  1  1  0  1    1  2  0  0  2  2  0  3

 inicial       objetivo         Hamming = 5 + 0          Manhattan = 10 + 0
```

Façamos uma observação chave: Para resolver o problema a partir de um dado nó de busca na fila de prioridade, o número total de movimentos que precisamos fazer (incluindo os que já foram feitos) é, pelo menos, sua prioridade, usando tanto a função de *Hamming*, quanto a de *Manhattan*. (na prioridade de Hamming isso é verdade porque cada casa que está fora de sua posição deve se mover pelo menos uma vez para chegar à sua posição de destino. Para a prioridade de Manhattan, é verdade porque cada casa deve se mover sua distância de Manhattan até a sua posição final. Perceba que não contamos a casa vazia quando computamos a função de Hamming ou de Manhattan.)

Consequentemente, quando o tabuleiro objetivo é desenfileirado, descobrimos não apenas a sequência de movimentos do tabuleiro inicial até o objetivo, mas uma que faz o menor número de movimentos.

**Uma otimização crítica:** A busca *best-first* tem uma característica irritante: os nós de busca correspondentes ao mesmo tabuleiro acabem sendo enfileirados na *priority queue* (fila de prioridade) muitas vezes. Para reduzir a exploração desnecessária de nós, quando considerar os vizinhos de um dado nó, não enfileire um vizinho se seu tabuleiro é **igual** ao **tabuleiro anterior**.

```
 8  1  3        8  1  3        8  1           8  1  3        8  1  3  
 4     2        4  2           4  2  3        4     2        4  2  5
 7  6  5        7  6  5        7  6  5        7  6  5        7  6

 anterior       nó atual       vizinho        vizinho        vizinho
                                           **(não permitir)**
```

**Árvore de jogo**. Uma forma de ver o cálculo é através de uma *árvore de jogo (game tree)*, onde cada nó de busca é um nó na árvore e os filhos de um nó correspondem a seus nós de busca vizinhos. A raiz da árvore de jogo é o nó de busca inicial; os nós internos já foram processados; os nós folha são mantidos em uma *[fila de prioridade](https://en.wikipedia.org/wiki/Priority_queue#:~:text=In%20computer%20science%2C%20a%20priority,an%20element%20with%20low%20priority.)*; em um dado momento, o algoritmo A* remove o nó com a menor prioridade  da fila e o processa (adicionando seus filhos tanto à árvore de jogo quanto à fila de prioridade).

![https://www.cs.princeton.edu/courses/archive/fall15/cos226/assignments/8puzzle-game-tree.png](https://www.cs.princeton.edu/courses/archive/fall15/cos226/assignments/8puzzle-game-tree.png)

**Detectando puzzles irresolvíveis**. Nem todos os tabuleiros iniciais levam ao tabuleiro objetivo usando-se uma sequencia de movimentos legais, incluindo os dois abaixo:

```
 1  2  3         1  2  3  4
 4  5  6         5  6  7  8
 8  7            9 10 11 12
                13 15 14 
irresolvível
                irresolvível
```

Para detectar essas situações, use o fato de que os tabuleiros são divididos em duas classes de equivalência, com respeito ao alcance: (i) aqueles que levam ao tabuleiro objetivo e (ii) aqueles que não podem levar ao tabuleiro objetivo. Além disso, podemos identificar a qual classe de equivalência um tabuleiro pertence *sem* nem tentar resolvê-lo.

- *Tabuleiro de tamanho ímpar*. Dado um tabuleiro, uma *inversão* é qualquer par de quadros *i* e *j* onde *i < j* mas *i* aparece **depois** de *j* quando consideramos o tabuleiro convertido em uma *linha*.

```
1  2  3             1  2  3             1  2  3             1  2  3             1  2  3
4  5  6    =>       4  5  6    =>       4     6    =>          4  6    =>       4  6  7
8  7                8     7             8  5  7             8  5  7             8  5 

1 2 3 4 5 6 8 7   1 2 3 4 5 6 8 7    1 2 3 4 6 8 5 7   1 2 3 4 6 8 5 7   1 2 3 4 6 7 8 5

inversões: 1      inversões: 1      inversões: 3       inversões: 3       inversões: 3
 (8-7)               (8-7)          (6-5 8-5 8-7)      (6-5 8-5 8-7)      (6-5 7-5 8-5)
```

Se o tamanho *N* do tabuleiro for um inteiro *ímpar*, então cada movimento legal muda o número de inversões por um número par. Assim, se um tabuleiro tem um número ímpar de inversões, então ele ***não pode*** levar ao tabuleiro objetivo com uma sequência de movimentos legais, porque o tabuleiro objetivo tem um número par de inversões (zero).

O oposto também é verdade. Se um tabuleiro tem um número par de inversões, então ele *pode* levar ao tabuleiro objetivo com uma sequência de movimentos legais.

```
       1  3           1     3            1  2  3            1  2  3            1  2  3
    4  2  5     =>    4  2  5     =>     4     5     =>     4  5        =>     4  5  6
    7  8  6           7  8  6            7  8  6            7  8  6            7  8 

1 3 4 2 5 7 8 6   1 3 4 2 5 7 8 6   1 2 3 4 5 7 8 6     1 2 3 4 5 7 8 6   1 2 3 4 5 6 7 8

 inversões: 4      inversões: 4      inversões: 2        inversões:2        inversões: 0
(3-2 4-2 7-6 8-6)  (3-2 4-2 7-6 8-6)  (7-6 8-6)          (7-6 8-6)
```

- *Tabuleiro de tamanho par.* Se o tamanho *N* do tabuleiro for um inteiro par, então a paridade do número de inversões não é invariante. Porém, a paridade do número de inversões *mais* a linha do quadrado branco é invariante: cada movimento legal muda essa soma em um número par. Se essa soma é par, então ela *não pode* levar ao tabuleiro objetivo com uma sequência de movimentos legais; se a soma é ímpar, então ela *pode* levar ao tabuleiro objetivo, com uma sequência de movimentos legais.

```
   1  2  3  4           1  2  3  4           1  2  3  4           1  2  3  4           1  2  3  4
   5     6  8     =>    5  6     8     =>    5  6  7  8     =>    5  6  7  8     =>    5  6  7  8
   9 10  7 11           9 10  7 11           9 10    11           9 10 11              9 10 11 12
  13 14 15 12          13 14 15 12          13 14 15 12          13 14 15 12          13 14 15

   linha bco = 1       linha bco  = 1       linha bco  = 2       linha bco  = 2       linha bco  = 3
   inversões = 6       inversões  = 6       inversões  = 3       inversões  = 3       inversões  = 0
  --------------       --------------       --------------       --------------       --------------
        soma = 7             soma = 7             soma = 5             soma = 5             soma = 3
.
```

**Tipos de dados para o tabuleiro e para o algoritmo que resolve o problema.** 

Organize seu programa criando um tipo imutável `Board` com a seguinte interface:

```java
public class Board {
    public Board(int[][] tiles);          // constrói um tabuleiro a partir de um array de quadros N-por-N
                                          // (onde tiles[i][j] = quadro na linha i, coluna j)
    public int tileAt(int i, int j);      // retorna o quadro na linha i, coluna j (ou 0 se vazio)
    public int size();                    // o tamanho N do tabuleiro
    public int hamming();                 // número de quadros fora do lugar
    public int manhattan();               // soma das distâncias de Manhattan entre os quadros e o objetivo final
    public boolean isGoal();              // este tabuleiro é o tabuleiro objetivo?
    public boolean isSolvable();          // este tabuleiro é resolvível?
    public boolean equals(Object y);      // este tabuleiro é igual ao tabuleiro y?
    public Iterable<Board> neighbors();   // todos os tabuleiros vizinhos
    public String toString();             // representação em string deste tabuleiro (no formato especificado abaixo)
}
```

*Detalhes de implementação.* 

Você pode assumir que o construtor recebe um array *N-por-N* contendo os $N^2$ inteiros entre 0 e $N^2-1$, onde 0 representa o quadrado vazio. O método `tileAt()` deve lançar uma exceção `java.lang.IndexOutOfBoundsException` a não ser que ambos `i` e `j` estejam entre 0 e $N-1$.

Crie também um tipo `Solver` com a seguinte interface:

```java
public class Solver {
    public Solver(Board initial);           // encontra a solução para o tabuleiro inicial (usando o algoritmo A*)
    public int moves();                     // min número de movimentos para resolver o tabuleiro inicial
    public Iterable<Board> solution();      // sequência de tabuleiros na solução mais curta
    public static void main(String[] args); // resolve o puzzle (fornecido abaixo)
}
```

Para implementar o algoritmo A*, utilize o tipo `PriorityQueue`, do pacote `java.util`.

*Detalhes de implementação.* 

O construtor deve lançar uma exceção `java.lang.IllegalArgumentException` se o tabuleiro inicial não for resolvível e uma exceção `java.lang.NullPointerException` se o tabuleiro inicial for `null`.

**Código para o método main do Solver**.

Você pode usar o código abaixo para ler o puzzle de um arquivo (especificado como um parâmetro da linha de comando) e imprimir a solução na saída padrão.

```java
public class Solver {
    public Solver(Board initial) {}
    private static int[][] loadFileIntoArray(String fileName) throws FileNotFoundException {
        Scanner in = new Scanner(new File(fileName));
        int n = in.nextInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.nextInt();
            }
        }
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
}
```

**Formatos de entrada e saída.** 

O formato de entrada e saída para um tabuleiro é o seu tamanho *N* seguido do tabuleiro inicial *N-por-N*, usando 0 para representar o quadro vazio;

```bash
% more puzzle04.txt
3
 0  1  3
 4  2  5
 7  8  6

% java Solver puzzle04.txr
Mínimo número de movimentos = 4
3
 0  1  3 
 4  2  5 
 7  8  6 

3
 1  0  3 
 4  2  5 
 7  8  6 

3
 1  2  3 
 4  0  5 
 7  8  6 

3
 1  2  3 
 4  5  0 
 7  8  6 

3
 1  2  3 
 4  5  6 
 7  8  0

% more unsolvable-puzzle3x3.txt
3
 1  2  3
 4  5  6
 8  7  0

% java Solver unsolvable-puzzle3x3.txt
Puzzle irresolvível
```

Seu programa deve funcionar corretamente para qualquer tabuleiro arbitrário de *N-por-N* (para qualquer $1\leq N\leq32768$), mesmo que seja muito lento para resolvê-lo.