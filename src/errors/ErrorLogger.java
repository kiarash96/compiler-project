package errors;

import lexical.Token;

/**
 * Created by kiarash on 6/7/17.
 */
public class ErrorLogger {
    public static final String LEXICAL_ERROR = "Lexical Error";
    public static final String SYNTAX_ERRO = "Syntax Error";
    public static final String SEMANTIC_ERROR = "Semantic Error";

    public static void printError(String errorType, Token token, String message) {
        System.out.println(errorType + " in line " + token.getLine() + " column " + (token.getColumn() - 1)
        + " near " + token.getLexeme() + ": " + message);
    }
}
