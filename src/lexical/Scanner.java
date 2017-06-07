package lexical;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by kiarash on 5/29/17.
 */
public class Scanner {

    private final static String delimiters = " ;,[]{}()=&<+-*/";

    private InputStream input;
    private int line, column;

    private static final int BUFFER_SIZE = 64;
    private byte[] buffer;

    private int lb, forward;
    private boolean revertFlag;
    private int state;

    public Scanner(InputStream input) {
        this.input = input;
        line = 1;
        column = 1;
        buffer = new byte[BUFFER_SIZE];
        lb = 0;
        forward = BUFFER_SIZE - 1;
        revertFlag = false;
        state = 0;
    }

    public Token getNextToken() throws IOException {
        reset();

        while (!nextState()); // go until reaching a final state or error

        switch (state) {
            case 2:
                revertForward();
                return getNextToken(); // whitespace

            case 4:
                revertForward();
                return new NumberToken(getLexeme());

            case 5:
                return new Token(Token.TokenType.EOF);

            case 7:
                revertForward();
                return new IDToken(getLexeme());

            case 11:
                return getNextToken(); // ignore comment token

            case 12:
            case 15:
                revertForward();
                return new SymbolToken(getLexeme());

            case 13:
            case 16:
            case 18:
                return new SymbolToken(getLexeme());

        }
        return new Token();
    }

    private String getLexeme() {
        if (lb <= forward)
            return new String(Arrays.copyOfRange(buffer, lb, forward + 1));
        else
            return new String(Arrays.copyOfRange(buffer, lb, BUFFER_SIZE))
                    .concat(new String(Arrays.copyOfRange(buffer, 0, forward + 1)));
    }

    private char moveForward() throws IOException {
        // 0 - 63   64 - 127
        if (!revertFlag) {
            if (forward == BUFFER_SIZE - 1 || forward == BUFFER_SIZE/2 - 1) {
                int offset, count;

                if (forward == BUFFER_SIZE - 1)
                    offset = 0;
                else // (forward == BUFFER_SIZE / 2 - 1)
                    offset = BUFFER_SIZE / 2;

                count = input.read(buffer, offset, BUFFER_SIZE / 2);
                if (count < BUFFER_SIZE / 2)
                    buffer[offset + count] = 0;
            }
        }

        forward = (forward + 1) % BUFFER_SIZE;

        char ch = (char)buffer[forward];
        if (!revertFlag) {
            if (ch == '\n') {
                line++;
                column = 1;
            } else
                column ++;
        }

        if (revertFlag)
            revertFlag = false;

        return ch;
    }

    private void revertForward() {
        forward = (BUFFER_SIZE + forward - 1) % BUFFER_SIZE;
        revertFlag = true;
    }

    private void reset() {
        lb = (forward + 1) % BUFFER_SIZE;
        state = 0;
    }

    private boolean nextState() throws IOException {
        char ch = moveForward();
        switch (state) {
            case 0:
                if (Character.isWhitespace(ch))
                    state = 1;
                else if (Character.isDigit(ch))
                    state = 3;
                else if ((int)ch == 0) // EOF
                    state = 5;
                else if (Character.isLetter(ch))
                    state = 6;
                else if (ch == '/')
                    state = 8;
                else if (";,[]{}()<+-*/".indexOf(ch) > -1)
                    state = 13;
                else if (ch == '=')
                    state = 14;
                else if (ch == '&')
                    state = 17;
                else {
                    printError(ch);
                    reset();
                }
                break;

            case 1:
                if (Character.isWhitespace(ch))
                    state = 1;
                else
                    state = 2;
                break;

            case 3:
                if (Character.isDigit(ch))
                    state = 3;
                else if (delimiters.indexOf(ch) > -1)
                    state = 4;
                else {
                    printError(ch);
                    reset();
                }
                break;

            case 6:
                if (Character.isLetterOrDigit(ch))
                    state = 6;
                else
                    state = 7;
                break;

            case 8:
                if (ch == '*')
                    state = 9;
                else
                    state = 12;
                break;

            case 9:
                if (ch == '*')
                    state = 10;
                else
                    state = 9;
                break;

            case 10:
                if (ch == '*')
                    state = 10;
                else if (ch == '/')
                    state = 11;
                else
                    state = 9;
                break;

            case 14:
                if (ch == '=')
                    state = 16;
                else
                    state = 15;
                break;

            case 17:
                if (ch == '&')
                    state = 18;
                else {
                    printError(ch);
                    reset();
                }
                break;
        }

        return Arrays.asList(2, 4, 5, 7, 11, 12, 13, 15, 16, 18, 20).contains(state);
    }

    private void printError(char ch) {
        System.out.println("Lexical error near line " + line + " column " + (column - 1)
        + ": Invalid character " + ch);
    }
}
