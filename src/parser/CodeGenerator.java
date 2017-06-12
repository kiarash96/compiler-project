package parser;

import SymbolTable.*;
import lexical.IDToken;
import lexical.*;
import lexical.Token;

import java.util.*;

/**
 * Created by User on 6/11/2017.
 */
public class CodeGenerator {
    public List<String> PB= new ArrayList<>();
    private Stack<Integer> semanticStack= new Stack<>();
    private Stack<Integer> ssType= new Stack<>(); //type 1 -> mostaghim
                                                  // type 2 -> meghdar sarih #
                                                  // type 3 -> gheire mostaghim @
    private SymbolTable table;
    public static int tempMem=500;
    public CodeGenerator(SymbolTable table){
        this.table=table;
    }
    public void TACgenerate(String gr, Token nextToken){
        int op1,op2,type1, type2, t;
        switch (gr){
            case "NEWID":
                Cell newCell=table.addToTable(nextToken);
                semanticStack.add(newCell.getMemAdr());
                break;
            case "NEWARR":
                // age nextToken number nabud error bede!
                SymbolTable.memLine+= ((NumberToken)nextToken).getValue()-1;
                int adr= semanticStack.pop();
                ssType.pop();
                Cell c=table.findByMemoryAdress(adr);
                c.cellType=Cell.Type.Array;
                c.size=((NumberToken)nextToken).getValue();
                SymbolTable.progLine++;
                break;
            case "POP":
                semanticStack.pop();
                ssType.pop();
                SymbolTable.progLine++;
                break;
            case "PID":
                // tarif shodane ghablesh check beshe
                Cell id=table.findByLexeme(((IDToken)nextToken).getLexeme());
                semanticStack.push(id.getMemAdr());
                ssType.push(1);
                break;
            case "ADD":
                op1=semanticStack.pop();
                type1=ssType.pop();
                op2=semanticStack.pop();
                type2=ssType.pop();
                t=getTemp();
                PB.add("(ADD, "+signedPrint(op1, type1)+", "+signedPrint(op2, type2)+", "+ t+ ")");
                semanticStack.push(t);
                ssType.push(1);
                SymbolTable.Memory[t/4]=SymbolTable.Memory[op1/4]+SymbolTable.Memory[op2/4];
                break;
            case "MULT":
                op1=semanticStack.pop();
                type1=ssType.pop();
                op2=semanticStack.pop();
                type2=ssType.pop();
                t=getTemp();
                PB.add("(MULT, "+signedPrint(op1, type1)+", "+signedPrint(op2, type2)+", "+ t+ ")");
                semanticStack.push(t);
                ssType.push(1);
                SymbolTable.Memory[t/4]=SymbolTable.Memory[op1/4]*SymbolTable.Memory[op2/4];
                break;
            case "DIV":
                op1=semanticStack.pop();
                type1=ssType.pop();
                op2=semanticStack.pop();
                type2=ssType.pop();
                t=getTemp();
                PB.add("(DIV, "+signedPrint(op1, type1)+", "+signedPrint(op2, type2)+", "+ t+ ")");
                semanticStack.push(t);
                ssType.push(1);
                SymbolTable.Memory[t/4]=((Integer)rightValue(op1, type1)/rightValue(op2, type2));
                break;
            case "SUB":
                op1=semanticStack.pop();
                type1=ssType.pop();
                op2=semanticStack.pop();
                type2=ssType.pop();
                t=getTemp();
                PB.add("(SUB, "+signedPrint(op1, type1)+", "+signedPrint(op2, type2)+", "+ t+ ")");
                semanticStack.push(t);
                ssType.push(1);
                SymbolTable.Memory[t/4]=rightValue(op1, type1)-rightValue(op2, type2);
                break;
            case "ASSIGN":
                op1=semanticStack.pop();
                op2=semanticStack.pop();
                type1=ssType.pop();
                type2=ssType.pop();
                PB.add("(ASSIGN, "+signedPrint(op1, type1)+", "+signedPrint(op2, type2)+")");
                //TODO
                break;
        }
        System.out.println("ss : "+semanticStack);
    }
    private int rightValue(int op1, int type1){
        switch (type1){
            case 1: return SymbolTable.Memory[op1/4];
            case 2: return op1;
            case 3: return SymbolTable.Memory[SymbolTable.Memory[op1/4]/4];
            default: return 0;
        }
    }
    private String signedPrint(int adr, int type){
        switch (type){
            case 1: return String.valueOf(adr);
            case 2: return "#"+adr;
            case 3: return "@"+adr;
            default: return "0";
        }
    }
    private int getTemp(){
        tempMem+=4;
        return tempMem-4;
    }
}
