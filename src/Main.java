import lexical.*;
import lexical.Scanner;
import parser.SLRparse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by kiarash on 5/30/17.
 */

public class Main {
    public static void main(String[] args) throws IOException {
        java.util.Scanner stdin = new java.util.Scanner(System.in);
        Scanner scanner = new Scanner(new FileInputStream("input.txt"));

        SLRparse p= new SLRparse();
        p.parse();

        /*while (true) {
            Token token = scanner.getNextToken();
            if (token.getTokenType() == Token.TokenType.EOF)
                break;
            else if (token.getTokenType() == Token.TokenType.KEYWORD)
                System.out.println("Keyword: " + ((KeywordToken) token).getKeywordType().name());
            else if (token.getTokenType() == Token.TokenType.ID)
                System.out.println("ID: " + ((IDToken)token).getLexeme());
            else if (token.getTokenType() == Token.TokenType.NUMBER)
                System.out.println("Number: " + ((NumberToken) token).getValue());
            else if (token.getTokenType() == Token.TokenType.SYMBOL)
                System.out.println("Symbol: " + ((SymbolToken) token).getSymbolType().toString()
                + " FollowedBySpace: " + ((SymbolToken) token).isFollowedBySpace());
            //System.out.println("Buffer: " + new String(scanner.buffer).replace('\n', ' ').replace((char)0, '!'));
            //stdin.next();
        }*/
    }
}
