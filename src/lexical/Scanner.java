package lexical;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by kiarash on 5/29/17.
 */
public class Scanner {

    private InputStream input;

    private static final int BUFFER_SIZE = 64;
    private byte[] buffer;

    private int lb, forward;
    private boolean revertFlag;
    private int state;

    public Scanner(InputStream input) {
        this.input = input;
        buffer = new byte[BUFFER_SIZE];
        lb = 0;
        forward = BUFFER_SIZE - 1;
        revertFlag = false;
        state = 0;
    }

    public Token getNextToken() throws IOException {
        lb = (forward + 1) % BUFFER_SIZE;
        state = 0;

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
            case 20:
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
        else
            revertFlag = false;

        forward = (forward + 1) % BUFFER_SIZE;
        return (char)buffer[forward];
    }

    private void revertForward() {
        forward = (BUFFER_SIZE + forward - 1) % BUFFER_SIZE;
        revertFlag = true;
    }

    private boolean nextState() throws IOException {
        // TODO: error handling?
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
                else if (";,[]{}()<*/".indexOf(ch) > -1)
                    state = 13;
                else if (ch == '=')
                    state = 14;
                else if (ch == '&')
                    state = 17;
                else if (ch == '+' || ch == '-')
                    state = 19;
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
                else
                    state = 4;
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
                break;

            case 19:
                if (Character.isDigit(ch))
                    state = 3;
                else
                    state = 20;
                break;
        }

        return Arrays.asList(2, 4, 5, 7, 11, 12, 13, 15, 16, 18, 20).contains(state);
    }
}
