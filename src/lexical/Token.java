package lexical;

/**
 * Created by kiarash on 5/29/17.
 */
public class Token {
    public enum TokenType {
        ID,     // both user identifiers and keywords
        NUMBER,
        SYMBOL,
        EOF,
        /*COMMENT,
        WHITESPACE,*/
    }

    TokenType tokenType;

    public Token() {

    }

    public Token(TokenType type) {
        this.tokenType = type;
    }

    public TokenType getTokenType() {
        return tokenType;
    }
}

