package parser;

import SymbolTable.SymbolTable;
import errors.ErrorLogger;
import lexical.*;
import lexical.Scanner;
import parser.generator.ParseTable;
import parser.generator.ParserGeneratorException;
import parser.generator.SLRTableGenerator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by Yeganeh on 6/11/2017.
 */

public class SLRParser {
    private Stack st;
    private ParseTable table;
    private lexical.Scanner scanner;
    private Token nextToken;
    private CodeGenerator cg;

    public SLRParser(String testname) throws IOException, ParserGeneratorException {
        this.st = new Stack();
        this.table = new SLRTableGenerator("grammar.txt").generate();
        this.scanner = new Scanner(new FileInputStream("tests/" + testname + ".c"));
        this.cg = new CodeGenerator();
    }

    public void parse() throws IOException {
        st.push(0);
        nextToken = scanner.getNextToken();

        while(true){
            int col=Arrays.asList(table.actionTableHead).indexOf(nextToken.getSpecificType());
            int row= (Integer) st.peek();

            String action= table.actionTable[row][col];

            if(action.equals("acc")){
                cg.TACgenerate("EOF",null);
                System.out.println("accept");
                System.out.println(cg);
                break;
            }
            if(action.isEmpty()){
                String message = "Unexpected token. expected ";
                for (int i = 0; i < table.actionTableHead.length; i ++)
                    if (!table.actionTable[row][i].isEmpty())
                        message += table.actionTableHead[i].toString() + ", ";
                ErrorLogger.printError(ErrorLogger.SYNTAX_ERROR, nextToken, message);
                if (nextToken.getTokenType() == Token.TokenType.EOF)
                    break;
                //System.out.println(cg);
                panicMode();

                continue;
            }
            switch (action.charAt(0)){
                case 's':
                    action=action.replace("s","");
                    int state= Integer.parseInt(action);
                    if(nextToken.getSpecificType()== SymbolToken.SymbolType.SEMICOLON  || nextToken.getSpecificType()== SymbolToken.SymbolType.OPEN_CURLY_BRACES) {
                        SymbolTable.progLine++; // ezafe shodan yek khat be barname!
                    }
                    nextToken = scanner.getNextToken();
                    st.push(nextToken.getSpecificType());
                    st.push(state);
//                    System.out.println("Shift "+ state+"\nParse Stack: "+st);
                    break;

                case 'r':
                    action=action.replace("r","");
                    int redGrammar = Integer.parseInt(action);
                    String LHS= table.grammar[redGrammar][0];
                    cg.TACgenerate(cgInput(LHS, redGrammar),nextToken);
                    int RHS= Integer.parseInt(table.grammar[redGrammar][1]);
                    pop(2*RHS,st);
                    int gotoRow=(Integer)st.peek();
                    st.push(LHS);
                    int gotoCol=Arrays.asList(table.gotoHead).indexOf(LHS);
                    st.push(table.gotoTable[gotoRow][gotoCol]);
//                    System.out.println("Reduce to "+redGrammar+" LHS: "+LHS+" \nParse Stack:"+st);
                    break;
            }
        }
    }
    private String cgInput(String LHS, int grNum){
        switch(grNum){
            case 0:
                return "EOF";
            case 5:
                return "POP";
            case 8:
            case 9:
                return "FUNEND";
            case 14:
                return "UPDATE";
            case 15:
                return "UPDATEARR";
            case 16:
                return "BLOCKE";
            case 26:
                return "PRINT";
            case 27:
                return "ASSIGN";
            case 30:
                return "JPF";
            case 31:
                return "JP";
            case 32:
                return "WHILE";
            case 33:
                return"VOIDRET";
            case 34:
                return "INTRET";
            case 35:
                return "CHECKID";
            case 39:
                return "AND";
            case 41:
                return "EQUAL";
            case 42:
                return "LESS";
            case 46:
                return "MULT";
            case 47:
                return "DIV";
            case 44:
                return "SUB";
            case 43:
                return "ADD";
            case 51:
                return "CHECKFUNC";
            case 72:
                return "JPFUNC";
            case 74:
                return "VOIDMATCH";
            case 75:
                return "FUNCINP";
            case 76:
                return "FUNCINP";
        }
        return LHS;
    }

    private void panicMode() throws IOException {
        while (true) {
            int state = (Integer) st.peek();
            List<Integer> ans = new ArrayList<>();

            for (int i = 0; i < table.gotoTable[state].length; i++) {
                if (table.gotoTable[state][i] != 0)// hame unha ke gotoshun khali nis ro dare barmigardune
                    ans.add(i);
            }
            if (!ans.isEmpty()) {
                        System.out.println("STATE:" +state);
                boolean handled = false;
                Token next;
                nextToken = scanner.getNextToken();
                while (!handled) {
                    next = nextToken;
                    if(next.getTokenType().equals(Token.TokenType.EOF)){
                        break;
                    }
                            System.out.println(next.getSpecificType());
                    for (int i = 0; i < ans.size(); i++) {
//                        System.out.println(table.gotoHead[ans.get(i)]);
//                        System.out.println(Arrays.asList(table.follows[ans.get(i)]));
                        if (Arrays.asList(table.follows[ans.get(i)]).indexOf(next.getSpecificType()) != -1) {
                            handled = true;
                            nextToken = next;
                            st.push(table.gotoHead[ans.get(i)]);
                            st.push(table.gotoTable[state][ans.get(i)]);

                            System.out.println(table.gotoHead[ans.get(i)]);
                            System.out.println(Arrays.asList(table.follows[ans.get(i)]));
//                            System.out.println("Pareser Stack after panic mode"+st);
                            break;
                        }
                    }if(!handled)
                    {
                        System.out.println("PANIC MODE ERROR: wrong input: "+next.getSpecificType()+"\nIgnore it until correct input");
                        nextToken = scanner.getNextToken();
                    }
                }
                break; //shak daram!
            } else {
                st.pop();
                if(st.size()==0)
                {
                    System.out.println("Panic Mode Could not handle this error!");
                    break;
                }
                System.out.println("PANIC MODE: Removed from parser stack: "+ st.pop());
            }
        }
    }
    private void pop(int num, Stack st){
        for(int i=0; i< num; i++)
            st.pop();
    }
}

