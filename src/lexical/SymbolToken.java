package lexical;

/**
 * Created by kiarash on 5/29/17.
 */
public class SymbolToken extends Token {
    public enum SymbolType {
        SEMICOLON,              // ;
        COMMA,                  // ,
        OPEN_BRACKET,           // [
        CLOSE_BRACKET,          // ]
        OPEN_CURLY_BRACES,      // {
        CLOSE_CURLY_BRACES,     // }
        OPEN_PARENTHESIS,       // (
        CLOSE_PARENTHESIS,      // )
        ASSIGNMENT,             // =
        AND_AND,                // &&
        EQUALITY,               // ==
        LESSTHAN,               // <
        PLUS,                   // +
        MINUS,                  // -
        STAR,                   // *
        SLASH,                  // /
    }

    SymbolType symbolType;
    boolean followedBySpace;

    public SymbolToken(String lexeme, boolean followedBySpace) {
        this.tokenType = TokenType.SYMBOL;
        this.followedBySpace = followedBySpace;
        switch (lexeme) {
            case ";":
                this.symbolType = SymbolType.SEMICOLON;
                break;
            case ",":
                this.symbolType = SymbolType.COMMA;
                break;
            case "[":
                this.symbolType = SymbolType.OPEN_BRACKET;
                break;
            case "]":
                this.symbolType = SymbolType.CLOSE_BRACKET;
                break;
            case "{":
                this.symbolType = SymbolType.OPEN_CURLY_BRACES;
                break;
            case "}":
                this.symbolType = SymbolType.CLOSE_CURLY_BRACES;
                break;
            case "(":
                this.symbolType = SymbolType.OPEN_PARENTHESIS;
                break;
            case ")":
                this.symbolType = SymbolType.CLOSE_PARENTHESIS;
                break;
            case "=":
                this.symbolType = SymbolType.ASSIGNMENT;
                break;
            case "&&":
                this.symbolType = SymbolType.AND_AND;
                break;
            case "==":
                this.symbolType = SymbolType.EQUALITY;
                break;
            case "<":
                this.symbolType = SymbolType.LESSTHAN;
                break;
            case "+":
                this.symbolType = SymbolType.PLUS;
                break;
            case "-":
                this.symbolType = SymbolType.MINUS;
                break;
            case "*":
                this.symbolType = SymbolType.STAR;
                break;
            case "/":
                this.symbolType = SymbolType.SLASH;
                break;
        }
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    public boolean isFollowedBySpace() {
        return followedBySpace;
    }
}
