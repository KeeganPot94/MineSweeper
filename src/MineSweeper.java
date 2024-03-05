import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class MineSweeper {

    private class MineTile extends JButton {

        // tile class variables
        int r;
        int c;

        // constructor
        public MineTile(int r, int c) {
            // tile object variables
            this.r = r;
            this.c = c;
        }
    }

    // Mine Sweeper class variables
    int tileSize = 70;
    int numRows = 8;
    int numCols = numRows;
    int boardWidth = numCols * tileSize;
    int boardHeight = numRows * tileSize;

    // game window objects
    JFrame frame = new JFrame("Mine Sweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    // mine variable
    int mineCount = 10;

    // 2d array within MineTile
    MineTile[][] board = new MineTile[numRows][numCols];

    // arraylist to hold mines
    ArrayList<MineTile> mineList;

    // random object
    Random random = new Random();

    // game win variables
    int tilesClicked = 0;
    Boolean gameOver = false;

    // constructor
    MineSweeper() {

        // game window properties
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout()); // divides game window into containers: NORTH, SOUTH, EAST, WEST, CENTER

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Sweep " + Integer.toString(mineCount) + " mines");
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel); // textLabel contained in textPanel
        frame.add(textPanel, BorderLayout.NORTH); // textPanel contained in frame (game window)

        boardPanel.setLayout(new GridLayout(numRows, numCols)); // 8x8
        frame.add(boardPanel); // boardPanel contained in frame (game window)

        // populate grid with MineTiles (JButton)
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                // MineTile object
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                // tile properties
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 35));
                // tile.setText("💣");
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();

                        // left click
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText() == "") {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        }
                        // right click
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            // place flag if tile is empty
                            if (tile.getText() == "" && tile.isEnabled()) {
                                tile.setText("⛳");
                            }
                            // remove flag if right clicked 2nd time
                            else if (tile.getText() == "⛳") {
                                tile.setText("");
                            }
                        }

                    }
                });
                boardPanel.add(tile); // tile contained in boardPanel
            }
        }

        // game window setVisible to ensure all components are loaded before window is
        // visible
        frame.setVisible(true);

        setMines();

    }

    // setMines function to populate board with mines
    void setMines() {

        mineList = new ArrayList<MineTile>();

        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows); // 0 to 7
            int c = random.nextInt(numCols); // 0 to 7

            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft -= 1;
            }
        }
    }

    // revealMines function to reveal mines when clicked
    void revealMines() {

        for (int i = 0; i < mineList.size(); i++) {
            MineTile tile = mineList.get(i);
            tile.setText("💣");
        }

        gameOver = true;
        textLabel.setText("Game Over!");

    }

    // checkMines function to disable button after clicked
    // and display if mines are nearby
    void checkMine(int r, int c) {

        // return if checked tile is out of bounds
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return;
        }

        // return if tile has been clicked before
        MineTile tile = board[r][c];
        if (!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked += 1;

        int minesFound = 0;

        // top 3
        minesFound += countMine(r - 1, c - 1); // top left
        minesFound += countMine(r - 1, c); // top
        minesFound += countMine(r - 1, c + 1); // top right

        // left & right
        minesFound += countMine(r, c - 1); // left
        minesFound += countMine(r, c + 1); // right

        // bottom 3
        minesFound += countMine(r + 1, c - 1); // bottom left
        minesFound += countMine(r + 1, c); // bottom
        minesFound += countMine(r + 1, c + 1); // bottom right

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound)); // display minesFound nearby
        } else {
            tile.setText(""); // display empty tile & reveal empty neighbor tiles

            // top 3
            checkMine(r - 1, c - 1); // top left
            checkMine(r - 1, c); // top
            checkMine(r - 1, c + 1); // top right

            // left & right
            checkMine(r, c - 1); // left
            checkMine(r, c + 1); // right

            // bottom 3
            checkMine(r + 1, c - 1); // bottom left
            checkMine(r + 1, c); // bottom
            checkMine(r + 1, c + 1); // bottom right
        }

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            textLabel.setText("All Mines Found!");
        }
    }

    // countMine function to track nearby mines
    int countMine(int r, int c) {

        // return 0 if checked tile is out of bounds
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }
        // return 1 if checked tile is held within mineList
        if (mineList.contains(board[r][c])) {
            return 1;
        }
        return 0;
    }

}
