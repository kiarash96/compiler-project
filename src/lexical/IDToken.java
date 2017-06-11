package lexical;

/**
 * Created by kiarash on 5/29/17.
 */
public class IDToken extends Token {
    String lexeme;

    public IDToken(String lexeme) {
        this.tokenType = TokenType.ID;
        this.lexeme = lexeme;
    }

    public String getLexeme() {
        return lexeme;
    }
}
