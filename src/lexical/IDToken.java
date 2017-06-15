package lexical;

/**
 * Created by kiarash on 5/29/17.
 */
public class IDToken extends Token {

    public IDToken(String lexeme, int line, int column) {
        super(lexeme, line, column);
        this.tokenType = TokenType.ID;
    }

}
