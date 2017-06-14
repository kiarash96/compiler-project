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
        PB.add("");//reserve to jump to main in future
        this.table=table;
    }
    public void TACgenerate(String gr, Token nextToken){
        int op1,op2,type1, type2, t;
        switch (gr){
            case "NEWID":
                System.out.println("NEWID");
                System.out.println(nextToken.getSpecificType());
                Cell newCell=table.addToTable(nextToken);
                semanticStack.push(newCell.getMemAdr());
                ssType.push(1);
                break;
            case "NEWARR":
                System.out.println("NEWARR");
                // age nextToken number nabud error bede!
                SymbolTable.memLine+= ((NumberToken)nextToken).getValue()-1;
                int adr= semanticStack.pop();
                ssType.pop();
                Cell c=table.findByMemoryAdress(adr);
                c.cellType=Cell.Type.Array;
                c.size=((NumberToken)nextToken).getValue();

                break;
            case "POP":
                System.out.println("POP");
                semanticStack.pop();
                ssType.pop();
                break;
            case "PID":
                // tarif shodane ghablesh check beshe
                Cell id=table.findByLexeme(((IDToken)nextToken).getLexeme());
                semanticStack.push(id.getMemAdr());
                ssType.push(1);
                break;
            case "PARR":
                int index=semanticStack.pop();
                int base=semanticStack.pop();
                ssType.pop();ssType.pop();
                adr=base+4*index;
                semanticStack.push(adr);
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
                SymbolTable.Memory[t/4]=rightValue(op1, type1)+rightValue(op2, type2);
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
                SymbolTable.Memory[t/4]= rightValue(op1, type1) *  rightValue(op2, type2);
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
                SymbolTable.Memory[op2/4]=rightValue(op1,type1);
                break;
            case "LESS":
                op1=semanticStack.pop();
                op2=semanticStack.pop();
                type1=ssType.pop();
                type2=ssType.pop();
                t=getTemp();
                if(rightValue(op1, type1)> rightValue(op2, type2)){//true
                        SymbolTable.Memory[t/4]=1;
                }else{
                        SymbolTable.Memory[t/4]=0;
                }
                PB.add("(LT, "+signedPrint(op2,type2)+", "+signedPrint(op1,type1)+", "+t+")");
                semanticStack.push(t);
                ssType.push(1);
                break;
            case "EQUAL":
                op1=semanticStack.pop();
                op2=semanticStack.pop();
                type1=ssType.pop();
                type2=ssType.pop();
                t=getTemp();
                if(rightValue(op1, type1)== rightValue(op2, type2)){//true
                    SymbolTable.Memory[t/4]=1;
                }else{
                    SymbolTable.Memory[t/4]=0;
                }
                PB.add("(EQ, "+signedPrint(op2,type2)+", "+signedPrint(op1,type1)+", "+t+")");
                semanticStack.push(t);
                ssType.push(1);
                break;
            case "AND":
                op1=semanticStack.pop();
                op2=semanticStack.pop();
                type1=ssType.pop();
                type2=ssType.pop();
                t=getTemp();
                if(rightValue(op1, type1)!=0 && rightValue(op2, type2)!=0){//true
                    SymbolTable.Memory[t/4]=1;
                }else{
                    SymbolTable.Memory[t/4]=0;
                }
                PB.add("(AND, "+signedPrint(op2,type2)+", "+signedPrint(op1,type1)+", "+t+")");
                semanticStack.push(t);
                ssType.push(1);
                break;
            case "CHECKSIGN"://Error handling
                if(((SymbolToken)nextToken).isFollowedBySpace()){
                    System.out.println("LEXICAL ERROR: Between number sign and value should not be space. Compiler Deletes it.");
                }
                break;
            case "PUSHNUM":
                semanticStack.push(((NumberToken)nextToken).getValue());
                ssType.push(2);
                break;

            case "PUSHNEGNUM":
                semanticStack.push(-((NumberToken)nextToken).getValue());
                ssType.push(2);
                break;
            case "JPMAIN": // az avale barname beparim be main!
                int main=semanticStack.peek();
                Cell mainFunc=table.findByMemoryAdress(main);
                if(mainFunc.token.getLexeme().equals("main")){
                    PB.set(0, "(JP, "+PB.size()+")");
                }
                break;
            case "PRINT":
                //TODO ERROR HANDLING: check if ss[top] is a number it can be void function!!!
                op1=semanticStack.pop();
                type1=ssType.pop();
                PB.add("(PRINT, "+rightValue(op1, type1)+")");
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