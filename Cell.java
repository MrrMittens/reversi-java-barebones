public class Cell {
    private char token;
    private boolean validMove;
    private boolean emptyCell;
    private boolean borderCell;
    private boolean animating;


// constructor
    public Cell() {
        token = ' ';
        validMove = false;
        emptyCell = true;
        borderCell = false;
        animating = false;
    }


// accessors
    public char getToken() {
        return token;
    }
    public boolean isValid() {
        return validMove;
    }
    public boolean isEmpty() {
        return emptyCell;
    }
    public boolean isBorder() {
        return borderCell;
    }
    public boolean inAnimation() {
        return animating;
    }


// mutators
    public void toggleValid() {
        if (validMove == false) {
            validMove = true;
        } else {
            validMove = false;
        }
    }
    public void toggleBorder() {
        borderCell = true;
        emptyCell = false;
    }
    public void addToken(char addToken) {
        token = addToken;
        emptyCell = false;
    }
    public void removeToken() {
        token = ' ';
        emptyCell = true;
    }
    public void flipToken() {
        if (token == 'W') {
            token = 'B';
        } else {
            token = 'W';
        }
    }
    public void toggleAnimation() {
        if (animating == false) {
            animating = true;
        } else {
            animating = false;
        }
    }
}