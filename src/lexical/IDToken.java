package lexical;

/**
 * Created by kiarash on 5/29/17.
 */
public class IDToken extends Token {
    public enum IDType {
        // user defined name
        NAME,

        // primitive types
        INT,
        VOID,

        // other keywords
        IF,
        ELSE,
        WHILE,
        RETURN,
    }

    IDType idType;
    String lexeme;

    public IDToken(String lexeme) {
        this.tokenType = TokenType.ID;
        this.lexeme = lexeme;
        switch (lexeme) {
            case "int":
                idType = IDType.INT;
                break;
            case "void":
                idType = IDType.VOID;
                break;
            case "if":
                idType = IDType.IF;
                break;
            case "else":
                idType = IDType.ELSE;
                break;
            case "while":
                idType = IDType.WHILE;
                break;
            case "return":
                idType = IDType.RETURN;
                break;
            default:
                idType = IDType.NAME;
                // TODO: update symbol table
                break;
        }
    }

    public IDType getIDType() {
        return idType;
    }

    public String getLexeme() {
        return lexeme;
    }
}
