/* *****************************************************************************
 * Description: Displays a visual representation of Conways game of life and simulates
 * generations in real time. Supports reading plaintext pattern files and
 * adjustible height / width display windows. Note: borders wrap around.
 *
 * Runtime arguments: width, height, filename, animation delay (ms), x, y
 * x and y are starting coordinates of pattern (default is 0,0)
 *
 * Dependencies: Requires Princeton's COS 226 standard library (algs4.jar), under
 * GNU General Public License v3. https://algs4.cs.princeton.edu/code/
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class Life {
    private static final int DEFAULT_ARGS = 4;
    private boolean[][] board;
    private final int width;
    private final int height;

    public Life(int width, int height, String fileName, int x, int y) {
        // initialize board
        this.width = width;
        this.height = height;
        board = new boolean[width][height];
        // read in file
        In file = new In(fileName);
        while (!file.isEmpty()) {
            String line = file.readLine();

            // skip comment lines
            if (line.charAt(0) == '!')
                continue;

            // read in board lines
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == '.')
                    board[i + x][y] = false;
                else
                    board[i + x][y] = true;
            }
            y++;
        }
    }

    // updates the board array, returns an long iterable which stores ints x and y
    // as coordiates which were changed.
    public Queue<Long> generation() {
        Queue<Long> changed = new Queue<>();
        int[][] sums = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // iterate through all the fields around the inner state
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if (board[(x + j + width) % width][(y + i + height) % height])
                            sums[x][y]++;
                    }
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean result = false;
                if (sums[x][y] == 3)
                    result = true;
                if (sums[x][y] == 4)
                    continue;

                if (result != board[x][y]) {
                    board[x][y] = result;
                    // encode ints x and y into one long
                    long c = (long) x << 32 | y & 0xFFFFFFFFL;
                    // enqueue
                    changed.enqueue(c);
                }
            }
        }


        // testing: everything changes
        // for (int y = 0; y < height; y++) {
        //     for (int x = 0; x < width; x++) {
        //         // encode ints x and y into one long
        //         long c = (long) x << 32 | y & 0xFFFFFFFFL;
        //         // enqueue
        //         changed.enqueue(c);
        //     }
        // }

        return changed;
    }

    private void drawCell(int x, int y) {
        if (board[x][y]) // cell is alive
            StdDraw.setPenColor(StdDraw.BLACK);
        else
            StdDraw.setPenColor(StdDraw.WHITE);

        StdDraw.filledSquare(x + 0.5, y + 0.5, 0.475);
    }

    // String representation of current board
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (board[x][y])
                    boardString.append('O');
                else
                    boardString.append('.');
            }
            boardString.append("\n");
        }


        return boardString.toString();
    }

    public static void main(String[] args) {
        int x = 0;
        int y = 0;
        int width = Integer.parseInt(args[0]);
        int height = Integer.parseInt(args[1]);
        String file = args[2];
        int delay = Integer.parseInt(args[3]);

        if (args.length != DEFAULT_ARGS) {
            // set x and y to custom values
            x = Integer.parseInt(args[args.length - 2]);
            y = Integer.parseInt(args[args.length - 1]);
        }

        StdOut.println("Width: " + width);
        StdOut.println("Height: " + height);
        StdOut.println("File: " + file);
        StdOut.println("Starting x: " + x);
        StdOut.println("Starting y: " + y);

        Life life = new Life(width, height, file, x, y);

        if (width < 101 && height < 101)
            StdOut.println("\nStarting board:\n" + life.toString());
        else
            StdOut.println("\nStarting board: too long to print");

        // set up drawing window
        // note: minimum resolution should be ~5 px per cel
        StdDraw.setCanvasSize(500, 500);
        StdDraw.setXscale(0, life.width);
        StdDraw.setYscale(life.height, 0);

        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);

        // draw initial seed
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                life.drawCell(j, i);
            }
        }

        StdDraw.show();

        while (true) {
            StdDraw.pause(delay);
            Queue<Long> change = life.generation();
            for (long c : change) {
                // decode long into ints
                int xCur = (int) (c >> 32);
                int yCur = (int) c;

                life.drawCell(xCur, yCur);
            }
            StdDraw.show();
        }
    }
}
