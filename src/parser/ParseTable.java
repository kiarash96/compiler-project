package parser;

import lexical.*;
import lexical.Scanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by User on 6/11/2017.
 */

public class ParseTable {

    public String [] gotoHead= new String []{
        "D", "V", "Type", "NEWARR","NEWID","E", "ADD", "T", "MULT", "F", "POP", "PID"
    };
    public int [][] gotoTable= new int[][]{
            {1,2,4,0,0,3,0,5,0,7,0,9},//0
            {0,0,0,0,0,0,0,0,0,0,0,0},//1
            {0,10,4,0,0,0,0,0,0,0,0,0},//2
            {0,0,0,0,0,0,0,0,0,0,0,0},//3
            {0,0,0,0,12,0,0,0,0,0,0,0},//4
            {0,0,0,0,0,0,0,0,0,0,0,0},//5
            {0,0,0,0,0,0,0,0,0,0,0,0},//6
            {0,0,0,0,0,0,0,0,0,0,0,0},//7
            {0,0,0,0,0,14,0,5,0,7,0,9},//8
            {0,0,0,0,0,0,0,0,0,0,0,0},//9
            {0,0,0,0,0,16,0,5,0,7,0,9},//10
            {0,0,0,0,0,0,0,17,0,7,0,9},//11
            {0,0,0,0,0,0,0,0,0,0,0,0},//12
            {0,0,0,0,0,0,0,0,0,19,0,9},//13
            {0,0,0,0,0,0,0,0,0,0,0,0},//14
            {0,0,0,0,0,0,0,0,0,0,0,0},//15
            {0,0,0,0,0,0,0,0,0,0,0,0},//16
            {0,0,0,0,0,0,21,0,0,0,0,0},//17
            {0,0,0,0,0,0,0,0,0,0,0,0},//18
            {0,0,0,0,0,0,0,0,24,0,0,0},//19
            {0,0,0,0,0,0,0,0,0,0,0,0},//20
            {0,0,0,0,0,0,0,0,0,0,0,0},//21
            {0,0,0,0,0,0,0,0,0,0,25,0},//22
            {0,0,0,26,0,0,0,0,0,0,0,0},//23
            {0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0},
    };
    public String [][] grammar = new String [][]{
        {"D" , "3"},
        {"D", "1"},
            {"V", "5"},
            {"V", "8"},
            {"Type","1"},
            {"NEWARR","0"},
            {"NEWID","0"},
            {"E", "4"},
            {"ADD", "0"},
            {"E", "1"},
            {"T", "4"},
            {"MULT", "0"},
            {"T", "1"},
            {"F", "3"},
            {"F", "2"},
            {"POP", "0"},
            {"PID", "0"}
    };
    public Object[] actionTableHead= new Object[]{
            Token.TokenType.ID, SymbolToken.SymbolType.SEMICOLON, SymbolToken.SymbolType.OPEN_BRACKET, Token.TokenType.NUMBER,
            SymbolToken.SymbolType.CLOSE_BRACKET, KeywordToken.KeywordType.INT,
            SymbolToken.SymbolType.PLUS, SymbolToken.SymbolType.STAR, SymbolToken.SymbolType.OPEN_PARENTHESIS,
            SymbolToken.SymbolType.CLOSE_PARENTHESIS, Token.TokenType.EOF
    };
    public String [][] actionTable= new String[][]{
            {"r17", "", "", "", "", "s6", "", "", "s8", "", ""},
            {"","", "", "",  "", "","","", "", "", "acc"},
            {"", "", "", "", "", "s6", "", "", "", "", ""},
            {"","", "", "",  "", "","s11","", "", "", "r2"},
            {"r7","", "", "", "", "","","", "", "", ""},
            {"", "", "", "", "", "","r10","s13", "", "r10", "r10"},
            {"r5","", "", "", "", "r5","r5","r5", "r5", "", "r5"},
            {"","", "", "", "", "","r13","r13", "", "r13", "r13"},
            {"r17","", "", "", "", "","","", "s8", "", ""},
            {"s15","",  "", "", "", "","","", "", "", ""},
            {"r17","",  "", "", "", "","","", "s8", "", ""},
            {"r17","",  "", "", "", "","","", "s8", "", ""},
            {"s18", "", "", "", "", "","","", "", "", ""},
            {"r17","",  "", "", "", "","","", "s8", "", ""},
            {"","",  "", "", "", "","s11","", "", "s20", ""},
            {"","",  "", "", "", "","r15","r15", "", "r15", "r15"},
            {"","",  "", "", "", "","s11","", "", "", "r1"},
            {"","",  "", "", "", "","r9","s13", "", "r9", "r9"},
            {"","s22",  "s23", "", "", "","","", "", "", ""},
            {"","",  "", "", "", "","r12","r12", "", "r12", "r12"},
            {"","",  "", "", "", "","r14","r14", "", "r14", "r14"},
            {"","",  "", "", "", "","r8","", "", "r8", "r8"},
            {"r16","", "", "",  "", "r16","r16","r16", "r16", "", "r16"},
            {"","", "", "r6",  "", "","","", "", "", ""},
            {"","",  "", "", "", "","r11","r11", "", "r11", "r11"},
            {"r3","", "", "",  "", "r3","r3","r3", "r3", "", "r3"},
            {"","", "", "s27",  "", "","","", "", "", ""},
            {"","", "", "",  "s28", "","","", "", "", ""},
            {"","s29", "", "",  "", "","","", "", "", ""},
            {"r4","", "", "",  "", "r4","r4","r4", "r4", "", "r4"},
    };
    public Object[][] follows= new Object[][]{
            {SymbolToken.SymbolType.PLUS, SymbolToken.SymbolType.CLOSE_PARENTHESIS, Token.TokenType.EOF},
            {SymbolToken.SymbolType.PLUS, SymbolToken.SymbolType.CLOSE_PARENTHESIS, SymbolToken.SymbolType.STAR, Token.TokenType.EOF},
            {SymbolToken.SymbolType.PLUS, SymbolToken.SymbolType.CLOSE_PARENTHESIS, SymbolToken.SymbolType.STAR, Token.TokenType.EOF}
    };
}
