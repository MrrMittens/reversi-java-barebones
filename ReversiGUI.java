import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class ReversiGUI {
    private final ReversiGame game;
    private JFrame frame;
    private Container c;

    private JLayeredPane gameBoard;
    private JLabel[][] gameState;
    private JButton[][] gameMoves;

    private JLabel whiteCounter;
    private JLabel blackCounter;
    private JLabel playerCounter;
    private JButton toggleAI;

    private ImageIcon whiteToken;
    private ImageIcon blackToken;
    private ImageIcon[] whiteFlip;
    private ImageIcon[] blackFlip;

    private ImageIcon noToken;
    private ImageIcon validMove;

    private int currentFrame;
    private int frameDelay;
    private ActionListener animationProcess;
    private Timer animation;


// constructor
    public ReversiGUI() {
        game = new ReversiGame();
        frame = new JFrame();
        c = frame.getContentPane();
        gameBoard = new JLayeredPane();
        gameState = new JLabel[6][6];
        gameMoves = new JButton[6][6];
        createGameComponents();
        whiteCounter = new JLabel("White Pieces: 2");
        blackCounter = new JLabel("Black Pieces: 2");
        playerCounter = new JLabel("Current Player: White");
        toggleAI = new JButton("vs AI: off");
        createAnimationFrames();
        noToken = new ImageIcon("empty.png");
        validMove = new ImageIcon("valid.png");
        createAnimation();
    }
    private void createGameComponents() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                gameState[row][col] = new JLabel();
                gameMoves[row][col] = new JButton();
            }
        }
    }
    private void createAnimationFrames() {
        whiteToken = new ImageIcon("w.png");
        blackToken = new ImageIcon("b.png");
        whiteFlip = new ImageIcon[5];
        blackFlip = new ImageIcon[5];
        for (int animationFrame = 0; animationFrame < 5; animationFrame++) {
            whiteFlip[animationFrame] = new ImageIcon("w" + animationFrame + ".png");
            blackFlip[animationFrame] = new ImageIcon("b" + animationFrame + ".png");
        }
    }
    private void createAnimation() {
        currentFrame = 0;
        frameDelay = 125;
        animationProcess = new ActionListener() {
            public void actionPerformed(ActionEvent a) {
                currentFrame = (currentFrame + 1) % 5;
                if (currentFrame == 0) {
                    animation.stop();
                    update();
                    if (toggleAI.getText().equals("vs AI: on") && game.getCurrentPlayer().equals("Black")) {
                        moveAI();
                    }
                }
                gameBoard.repaint();
                for (int row = 0; row < 6; row++) {
                    for (int col = 0; col < 6; col++) {
                        if (game.getBoard()[row + 1][col + 1].inAnimation()) {
                            if (game.getCurrentPlayer().equals("White")) {
                                gameState[row][col].setIcon(whiteFlip[currentFrame]);
                            } else {
                                gameState[row][col].setIcon(blackFlip[currentFrame]);
                            }
                        }
                    }
                }
            }
        };
        animation = new Timer(frameDelay, animationProcess);
    }


// GUI setup
    public void setUpGUI() {
        c.setLayout(null);

        setUpComponents();
        updateGameComponents();
        updateGameState();
        updateGameMoves();

        frame.setSize(615, 670);
        frame.setTitle("Reversi 6x6");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    private void setUpComponents() {
        gameBoard.setBounds(30, 60, 540, 540);
        gameBoard.setLayout(null);
        c.add(gameBoard);

        whiteCounter.setBounds(30, 25, 100, 20);
        c.add(whiteCounter);

        blackCounter.setBounds(150, 25, 100, 20);
        c.add(blackCounter);

        playerCounter.setBounds(270, 25, 150, 20);
        c.add(playerCounter);

        toggleAI.setBounds(470, 25, 100, 20);
        c.add(toggleAI);
    }


// ButtonListener setup
    public void setUpButtonListener() {
        ActionListener buttonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent p) {
                Object button = p.getSource();
                
                if (button == toggleAI) {
                    if (toggleAI.getText().equals("vs AI: off")) {
                        toggleAI.setText("vs AI: on");
                    } else {
                        toggleAI.setText("vs AI: off");
                    }
                    return;
                }

                for (int row = 0; row < 6; row++) {
                    for (int col = 0; col < 6; col++) {
                        if (button == gameMoves[row][col]) {
                            playerMove(row, col);
                        }
                    }
                }
            }
        };
        toggleAI.addActionListener(buttonListener);
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                gameMoves[row][col].addActionListener(buttonListener);
            }
        }
    }


// update
    private void update() {
        game.endAnimatingTokens();
        game.updateValidMoves();
        updateGameComponents();
        updateGameMoves();
        updateTokenCounters();
        gameBoard.repaint();
    }
    private void clear() {
        for (Component comp : gameBoard.getComponents()) {
            if (comp instanceof JButton) {
                gameBoard.remove(comp);
            }
        }
    }
    private void updateGameComponents() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                switch(game.getBoard()[row + 1][col + 1].getToken()) {
                    case ' ': {
                        gameState[row][col].setIcon(noToken);
                        break;
                    }
                    case 'W': {
                        gameState[row][col].setIcon(whiteToken);
                        break;
                    }
                    case 'B': {
                        gameState[row][col].setIcon(blackToken);
                        break;
                    }
                }
                if (game.getBoard()[row + 1][col + 1].isValid()) {
                    gameMoves[row][col].setIcon(validMove);
                }
            }
        }
    }
    private void updateGameState() {
        for (int row = 0; row < gameState.length; row++) {
            for (int col = 0; col < gameState[row].length; col++) {
                gameBoard.add(gameState[row][col]);
                gameState[row][col].setBounds(0 + 90*row, 0 + 90*col, 90, 90);
                gameBoard.setLayer(gameState[row][col], 0);
            }
        }
    }
    private void updateGameMoves() {
        for (int row = 0; row < gameState.length; row++) {
            for (int col = 0; col < gameState[row].length; col++) {
                if (game.getBoard()[row + 1][col + 1].isValid()) {
                    gameBoard.add(gameMoves[row][col]);
                    gameMoves[row][col].setBounds(2 + 90*row, 2 + 90*col, 86, 86);
                    gameBoard.setLayer(gameMoves[row][col], 1);
                }
            }
        }
    }
    private void updateTokenCounters() {
        int[] counters = game.trackTokens();
        whiteCounter.setText("White Tokens: " + counters[1]);
        blackCounter.setText("Black Tokens: " + counters[2]);
        playerCounter.setText("Current Player: " + game.getCurrentPlayer());
        if (game.hasWinner()) {
            if (counters[1] < counters[2]) {
                playerCounter.setText("Black Wins.");
            } else if (counters[1] > counters[2]) {
                playerCounter.setText("White Wins.");
            } else {
                playerCounter.setText("Game is tied.");
            }
        }
    }


// input management
    private void playerMove(int xInput, int yInput) {
        if (game.getCurrentPlayer().equals("White")) {
            gameState[xInput][yInput].setIcon(whiteToken);
        } else {
            gameState[xInput][yInput].setIcon(blackToken);
        }
        game.placeToken(xInput + 1, yInput + 1);
        clear();
        animation.start();
    }
    private void moveAI() {
        ArrayList<int[]> decisions = new ArrayList<>();
        Random choose = new Random();
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                if (game.getBoard()[row + 1][col + 1].isValid()) {
                    int[] choice = {row, col};
                    decisions.add(choice);
                }
            }
        }
        int[] decided = decisions.get(choose.nextInt(decisions.size()));
        playerMove(decided[0], decided[1]);
    }
}