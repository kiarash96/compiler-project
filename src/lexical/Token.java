package lexical;

import java.util.*;


import static lexical.Token.TokenType.*;

/**
 * Created by kiarash on 5/29/17.
 */
public class Token {
    public enum TokenType{
        ID,     // both user identifiers and keywords
        NUMBER,
        SYMBOL,
        EOF,
        KEYWORD
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

    public Object getSpecificType() {
        switch (this.tokenType) {
            case SYMBOL:
                return ((SymbolToken) this).symbolType;
            case KEYWORD:
                return ((KeywordToken) this).keywordType;
            default:
                return this.tokenType;
        }
    }
}

