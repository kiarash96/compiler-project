package parser;

import SymbolTable.*;
import lexical.IDToken;
import lexical.NumberToken;
import lexical.Token;

import java.util.*;

/**
 * Created by User on 6/11/2017.
 */
public class CodeGenerator {
    private List<String> PB= new ArrayList<>();
    private Stack<Integer> semanticStack= new Stack<>();
    private SymbolTable table;
    public static int tempMem=500;
    public CodeGenerator(SymbolTable table){
        this.table=table;
    }
    public void TACgenerate(String gr, Token nextToken){
        switch (gr){
            case "NEWID":
                Cell newCell=table.addToTable(nextToken);
                semanticStack.add(newCell.getMemAdr());
                break;
            case "NEWARR":
                // age nextToken number nabud error bede!
                SymbolTable.memLine+= 4*(((NumberToken)nextToken).getValue()-1);
                int adr= semanticStack.pop();
                Cell c=table.findByMemoryAdress(adr);
                c.cellType=Cell.Type.Array;
                c.size=((NumberToken)nextToken).getValue();
                SymbolTable.progLine++;
                break;
            case "POP":
                semanticStack.pop();
                SymbolTable.progLine++;
                break;
            case "PID":
                // tarif shodane ghablesh check beshe
                Cell id=table.findByLexeme(((IDToken)nextToken).getLexeme());
                semanticStack.push(id.getMemAdr());
                break;
            case "ADD":
                int op1=semanticStack.pop();
                int op2=semanticStack.pop();
                int t=getTemp();
                PB.add("(+, "+op1+", "+op2+", "+ t+ ")");
                semanticStack.push(t);
                break;
            case "MULT":
                op1=semanticStack.pop();
                op2=semanticStack.pop();
                t=getTemp();
                PB.add("(+, "+op1+", "+op2+", "+ t+ ")");
                semanticStack.push(t);
                break;
        }
        System.out.println(semanticStack);
    }
    private int getTemp(){
        tempMem++;
        return tempMem;
    }
}
