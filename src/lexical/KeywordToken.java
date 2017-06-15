package lexical;

/**
 * Created by User on 6/11/2017.
 */
public class KeywordToken extends Token{
    public enum KeywordType {
        // primitive types
        INT,
        VOID,

        // other keywords
        IF,
        ELSE,
        WHILE,
        RETURN,
        OUTPUT,

        // not a keyword
        INVALID,
    }
    KeywordType keywordType;
    String lexeme;

    public KeywordToken(String lexeme) {
        this.tokenType = TokenType.KEYWORD;
        this.lexeme = lexeme;
        switch (lexeme) {
            case "int":
                keywordType = KeywordToken.KeywordType.INT;
                break;
            case "void":
                keywordType = KeywordToken.KeywordType.VOID;
                break;
            case "if":
                keywordType = KeywordToken.KeywordType.IF;
                break;
            case "else":
                keywordType = KeywordToken.KeywordType.ELSE;
                break;
            case "while":
                keywordType = KeywordToken.KeywordType.WHILE;
                break;
            case "return":
                keywordType =KeywordToken.KeywordType.RETURN;
                break;
            case "output":
                keywordType = KeywordType.OUTPUT;
                break;
            default:
                keywordType = KeywordType.INVALID;
        }
    }

    public KeywordType getKeywordType() {
        return keywordType;
    }

}
