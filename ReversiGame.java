public class ReversiGame {
    private int size;
    private Cell[][] board;
    private int turnCounter;
    private boolean winner;

// constructor
    public ReversiGame() {
        size = 6;
        board = new Cell[size+2][size+2];
        turnCounter = 0;
        winner = false;
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = new Cell();
            }
        }
        initializeBorder();
        initializeTokens();
        placeValidMoves();
    }
    private void initializeBorder() {
        boolean gridEdge;
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                gridEdge = (row == 0 || row == board.length-1) || (col == 0 || col == board[row].length-1);
                if (gridEdge) {
                    board[row][col].toggleBorder();
                }
            }
        }
    }
    private void initializeTokens() {
        board[3][3].addToken('W');
        board[4][4].addToken('W');
        board[3][4].addToken('B');
        board[4][3].addToken('B');
    }


// accessors
    public Cell[][] getBoard() {
        return board;
    }
    public boolean hasWinner() {
        return winner;
    }
    public String getCurrentPlayer() {
        String player;
        if (currentTurn()) {
            player = "White";
        } else {
            player = "Black";
        }
        return player;
    }


// mutators
    public void placeToken(int xPos, int yPos) { // takes in a coordinate input and executes flipping process
        board[xPos][yPos].addToken(getCurrentToken(currentTurn()));
        flipTokens(xPos,yPos);
        turnCounter++;
    }
    private void flipTokens(int xPos, int yPos) { // 
        surroundCheck(xPos, yPos, true);
    }

    
    public boolean updateValidMoves() { // displays cells current player can place tokens
        resetValidMoves();
        placeValidMoves();
        if (noValidMoves()) {
            return false;
        }
        return true;
    }
    private void resetValidMoves() { // removes all valid moves
        for (int row = 1; row < board.length-1; row++) {
            for (int col = 1; col < board[row].length-1; col++) {
                if (board[row][col].isValid()) {
                    board[row][col].toggleValid();
                }
            }
        }
    }
    private void placeValidMoves() { // re-computes valid moves through surroundCheck and tokenCrawl method, toggles cell validity
        for (int row = 1; row < board.length-1; row++) {
            for (int col = 1; col < board[row].length-1; col++) {
                if (board[row][col].getToken() == getCurrentToken(currentTurn())) {
                    surroundCheck(row, col, false);
                }
            }
        }
    }
    private boolean noValidMoves() { // if there are no valid move found, skip the turn
        for (int row = 1; row < board.length-1; row++) {
            for (int col = 1; col < board[row].length-1; col++) {
                if (board[row][col].isValid()) {
                    return false;
                }
            }
        }
        return true;
    }


// supporting methods
    private boolean currentTurn() { // returns true during white's turn, otherwise false
        if (turnCounter % 2 == 0) {
            return true;
        } else {
            return false;
        }
    }
    private char getCurrentToken(boolean whitesTurn) { // returns token of corresponding turn
        if (whitesTurn) {
            return 'W';
        } else {
            return 'B';
        }
    }
    private void surroundCheck(int xPos, int yPos, boolean flip) { // checks surrounding for getCurrentToken(!turn)
        for (int zRow = -1; zRow <= 1; zRow++) {
            for (int zCol = -1; zCol <= 1; zCol++) {
                if (board[xPos + zRow][yPos + zCol].getToken() == getCurrentToken(!currentTurn())) {
                    int[] direction = {zRow, zCol};
                    tokenCrawl(xPos, yPos, direction, flip);
                }
            }
        }
    }
    private void tokenCrawl(int xPos, int yPos, int[] direction, boolean flip) {
        boolean endPoint;
        do { 
            xPos += direction[0];
            yPos += direction[1];
            endPoint = !(board[xPos][yPos].getToken() == getCurrentToken(!currentTurn()));
        } while (!endPoint);
        
        if (board[xPos][yPos].isEmpty() && !flip && !board[xPos][yPos].isValid()) {
            board[xPos][yPos].toggleValid();
        } 
        if (board[xPos][yPos].getToken() == getCurrentToken(currentTurn()) && flip) {
            int[] invertedDirection = {-direction[0], -direction[1]};
            crawlFlip(xPos, yPos, invertedDirection);
        }
    }
    private void crawlFlip(int xPos, int yPos, int[] invertedDirection) {
        boolean endPoint;
        xPos += invertedDirection[0];
        yPos += invertedDirection[1];
        do { 
            board[xPos][yPos].flipToken();
            if (!board[xPos][yPos].inAnimation()) {
                board[xPos][yPos].toggleAnimation();
            }
            xPos += invertedDirection[0];
            yPos += invertedDirection[1];
            endPoint = board[xPos][yPos].getToken() == getCurrentToken(currentTurn());
        } while (!endPoint);
    }


// additional methods
    public void endAnimatingTokens() {
        for (int row = 1; row < board.length-1; row++) {
            for (int col = 1; col < board[row].length-1; col++) {
                if (board[row][col].inAnimation()) {
                    board[row][col].toggleAnimation();
                }
            }
        }
    }
    public int[] trackTokens() {
        int blackTokens = 0;
        int whiteTokens = 0;
        int emptySpaces = 0;
        for (int row = 1; row < board.length-1; row++) {
            for (int col = 1; col < board[row].length-1; col++) {
                switch(board[row][col].getToken()) {
                    case 'B': {
                        blackTokens++;
                        break;
                    }
                    case 'W': {
                        whiteTokens++;
                        break;
                    }
                    case ' ': {
                        emptySpaces++;
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        }
        int[] counters = {emptySpaces, whiteTokens, blackTokens};
        updateWinner(emptySpaces, whiteTokens, blackTokens);
        return counters;
    }
    private void updateWinner(int emptySpaces, int whiteTokens, int blackTokens) {
        if (emptySpaces == 0) {
            winner = true;
        } else if (whiteTokens == 0 || blackTokens == 0) {
            winner = true;
        } else if (noValidMoves()) {
            turnCounter += 1;
            if (!updateValidMoves()) {
                winner = true;
            }
        }
    }
}

// methods to be used in GUI:
    // placeToken(x, y) === main game process, placing and flipping
    // updateValidMoves() === initializes turn, button input reference
    // trackTokens() === updates winner boolean and used as token counter, skips a turn when no valid moves

    // getBoard() === main reference for GUI layout
    // hasWinner() === manages game loop