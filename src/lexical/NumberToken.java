package lexical;

/**
 * Created by kiarash on 5/30/17.
 */
public class NumberToken extends Token {
    int value;

    public NumberToken(String lexeme, int line, int column) {
        super(lexeme, line, column);
        this.tokenType = TokenType.NUMBER;
        this.value = Integer.valueOf(lexeme);
    }

    public int getValue() {
        return value;
    }
}
