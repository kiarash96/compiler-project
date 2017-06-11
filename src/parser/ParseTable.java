package parser;

import lexical.Scanner;
import lexical.SymbolToken;
import lexical.Token;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by User on 6/11/2017.
 */

public class ParseTable {

    public String [] gotoHead= new String []{
        "Expression", "Term", "Factor"
    };
    public int [][] gotoTable= new int[][]{
            {1,2,3},
            {0,0,0},
            {0,0,0},
            {0,0,0},
            {8,2,3},
            {0,0,0},
            {0,9,3},
            {0,0,10},
            {0,0,0},
            {0,0,0},
            {0,0,0},
            {0,0,0}
    };
    public String [][] grammar = new String [][]{
        {"Expression" , "3"},
        {"Expression", "1"},
            {"Term", "3"},
            {"Term", "1"},
            {"Factor","3"},
            {"Factor", "1"}
    };
    public Object[] actionTableHead= new Object[]{
            Token.TokenType.ID, SymbolToken.SymbolType.PLUS, SymbolToken.SymbolType.STAR, SymbolToken.SymbolType.OPEN_PARENTHESIS,
            SymbolToken.SymbolType.CLOSE_PARENTHESIS, Token.TokenType.EOF
    };
    public String [][] actionTable= new String[][]{
            {"s5", "", "", "s4", "", ""},
            {"", "s6", "", "", "", "acc"},
            {"", "r2", "s7", "", "r2", "r2"},
            {"", "r4", "r4", "", "r4", "r4"},
            {"s5", "", "", "s4", "", ""},
            {"", "r6", "r6", "", "r6", "r6"},
            {"s5", "", "", "s4", "", ""},
            {"s5", "", "", "s4", "", ""},
            {"", "s6", "", "", "s11", ""},
            {"", "r1", "s7", "", "r1", "r1"},
            {"", "r3", "r3", "", "r3", "r3"},
            {"", "r5", "r5", "", "r5", "r5"}
    };
    public Object[][] follows= new Object[][]{
            {SymbolToken.SymbolType.PLUS, SymbolToken.SymbolType.CLOSE_PARENTHESIS, Token.TokenType.EOF},
            {SymbolToken.SymbolType.PLUS, SymbolToken.SymbolType.CLOSE_PARENTHESIS, SymbolToken.SymbolType.STAR, Token.TokenType.EOF},
            {SymbolToken.SymbolType.PLUS, SymbolToken.SymbolType.CLOSE_PARENTHESIS, SymbolToken.SymbolType.STAR, Token.TokenType.EOF}
    };
}
