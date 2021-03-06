package parser;

import SymbolTable.*;
import errors.ErrorLogger;
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
    public CodeGenerator(){
        PB.add(""); // reserve to set main return addr
        PB.add("");//reserve to jump to main in future
        this.table= new SymbolTable();
    }
    public void TACgenerate(String gr, Token nextToken){
        int op1,op2,type1, type2, t,size, inpSize=0;
        IDToken newInp;
//        System.out.println("Symbol Action:"+gr);
//        System.out.println( table); //print symbolTable
//        System.out.println("ss : "+semanticStack);
        switch (gr){
            case "NEWID":
//                System.out.println("NEWID");
//                System.out.println(nextToken.getSpecificType());
                Cell newCell=table.addToTable(nextToken);
                semanticStack.push(newCell.getMemAdr());
                ssType.push(1);
                break;
            case "NEWARR":
                size = ((NumberToken) nextToken).getValue();
                if (size <= 0) {
                    ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, nextToken, "Array size must be positive");
                    int adr = semanticStack.pop();
                    ssType.pop();
                    Cell c=table.findByMemoryAdress(adr);
                    c.cellType=Cell.Type.Array;
                    c.size= 0;
                    break;
                }
                SymbolTable.memLine+= ((NumberToken)nextToken).getValue()-1;
                int adr= semanticStack.pop();
                ssType.pop();
                Cell c=table.findByMemoryAdress(adr);
                c.cellType=Cell.Type.Array;
                c.size=((NumberToken)nextToken).getValue();
                break;
            case "POP":
                semanticStack.pop();
                ssType.pop();
                break;
            case "PID":
                Cell id=table.findByLexeme(((IDToken)nextToken).getLexeme());
                if (id == null) {
                    ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, nextToken, "Identifier is not defined.");
                    semanticStack.push(getTemp());
                    ssType.push(1);
                } else {
                    /*System.out.println("PID " + id.token.getLexeme() + " mem is " + id.getMemAdr());
                    if (id.getMemAdr() == -1) {
                        System.out.println("line is " + id.getLine());
                    }*/
                    if (id.getMemAdr() != -1)
                        semanticStack.push(id.getMemAdr());
                    else
                        semanticStack.push(((FunctionCell) id).returnValueAdr);
                    ssType.push(1);
                }
                break;
            case "PARR":
                int index=semanticStack.pop();
                int base=semanticStack.pop();
                int indexType = ssType.pop();ssType.pop();
                c=table.findByMemoryAdress(base);
                if (c == null)
                    c = table.findByRetAddr(base);
                if (c.cellType != Cell.Type.Array
                        && c.cellType != Cell.Type.DynamicArray) {
                    ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, nextToken, "Cannot use [] on identifier " + c.token.getLexeme());
                    semanticStack.push(getTemp());
                    ssType.push(1);
                } else {

                    if (c.cellType.equals(Cell.Type.DynamicArray)) {
                        int t1 = getTemp();
                        int t2 = getTemp();
                        PB.add("(MULT, #4, "
                                + signedPrint(index, indexType) + ", " + t1 + ")");
                        PB.add("(ADD, " + signedPrint(base, 3) + ", "
                                + t1 + ", " + t2 + ")");
                        semanticStack.push(t2);
                        ssType.push(3);
                    } else { // normal arr
                        size = table.findByMemoryAdress(base).size;
                        int t1 = getTemp();
                        int t2 = getTemp();
                        int t3 = getTemp();
                        PB.add("(LT, " + signedPrint(index, indexType) + ", "
                                + signedPrint(size, 2) + ", " + t1 + ")");
                        PB.add("(JPF, " + t1 + ", " + (PB.size() + 4) + ")");
                        PB.add("(MULT, #4, "
                                + signedPrint(index, indexType) + ", " + t2 + ")");
                        PB.add("(ADD, " + signedPrint(base, 2) + ", "
                                + t2 + ", " + t3 + ")");
                        PB.add("(JP, " + (PB.size() + 2) + ")");
                        PB.add("(PRINT, \"Array index out of bound\")");
                        semanticStack.push(t3);
                        ssType.push(3);
                    }
                }
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
//                SymbolTable.Memory[t/4]=rightValue(op1, type1)+rightValue(op2, type2);
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
//                SymbolTable.Memory[t/4]= rightValue(op1, type1) *  rightValue(op2, type2);
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
//                SymbolTable.Memory[t/4]=((Integer)rightValue(op1, type1)/rightValue(op2, type2));
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
//                SymbolTable.Memory[t/4]=rightValue(op1, type1)-rightValue(op2, type2);
                break;
            case "ASSIGN":
                op1=semanticStack.pop();
                op2=semanticStack.pop();
                type1=ssType.pop();
                type2=ssType.pop();
                PB.add("(ASSIGN, "+signedPrint(op1, type1)+", "+signedPrint(op2, type2)+")");
//                SymbolTable.Memory[op2/4]=rightValue(op1,type1);
                break;
            case "LESS":
                op1=semanticStack.pop();
                op2=semanticStack.pop();
                type1=ssType.pop();
                type2=ssType.pop();
                t=getTemp();
//                if(rightValue(op1, type1)> rightValue(op2, type2)){//true
//                        SymbolTable.Memory[t/4]=1;
//                }else{
//                        SymbolTable.Memory[t/4]=0;
//                }
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
//                if(rightValue(op1, type1)== rightValue(op2, type2)){//true
//                    SymbolTable.Memory[t/4]=1;
//                }else{
//                    SymbolTable.Memory[t/4]=0;
//                }
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
//                if(rightValue(op1, type1)!=0 && rightValue(op2, type2)!=0){//true
//                    SymbolTable.Memory[t/4]=1;
//                }else{
//                    SymbolTable.Memory[t/4]=0;
//                }
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
                Cell mainFunc=table.findByRetAddr(main);
                if(mainFunc.token.getLexeme().equals("main")){
                    PB.set(1, "(JP, "+PB.size()+")");
                }
                break;
            case "PRINT":
                op1=semanticStack.pop();
                type1=ssType.pop();
                PB.add("(PRINT, "+signedPrint(op1,type1)+")");
                break;
            case "LABEL":
                size= PB.size();
                semanticStack.push(size);
                ssType.push(1);
                break;
            case "SAVE":
                size= PB.size();
                PB.add("");
                semanticStack.push(size);
                ssType.push(1);
                break;
            case "WHILE":
                op1=semanticStack.pop();// where we write jpf
                op2=semanticStack.pop();// while condition
                ssType.pop();type2=ssType.pop();
                size=PB.size();
                PB.set(op1,"(jpf, "+signedPrint(op2,type2)+", "+(size+1)+")");
                PB.add("(jp, "+ signedPrint(semanticStack.pop(),ssType.pop())+")");
                break;
            case "JPF_SAVE":
                ssType.pop();
                size=PB.size();
                PB.set(semanticStack.pop(), "(jpf, "+signedPrint(semanticStack.pop(), ssType.pop())+", "+(size+1)+")");
                semanticStack.push(size);
                PB.add("");
                ssType.push(1);
                break;
            case "JPF":
                ssType.pop();
                size=PB.size();
                PB.set(semanticStack.pop(), "(jpf, "+signedPrint(semanticStack.pop(), ssType.pop())+", "+size+")");
                break;
            case "JP":
                ssType.pop();
                size=PB.size();
                PB.set(semanticStack.pop(), "(jp, "+size+")");
                break;
            case "BLOCKS":
                table.newScope();
                break;
            case "BLOCKE":
                table.deleteScope();
                break;
            case "INTFUNC":
                adr =semanticStack.pop();
                ssType.pop();
                FunctionCell fc=table.funcAddToTable(adr, FunctionCell.returnType.Int);
                table.currentFunc = fc;
                semanticStack.push(fc.returnValueAdr);
                ssType.push(1);
//                SymbolTable.progLine++;
                table.newScope();
                break;
            case "VOIDFUNC":
                adr =semanticStack.pop();
                ssType.pop();
                fc = table.funcAddToTable(adr, FunctionCell.returnType.Void);
                table.currentFunc = fc;
                semanticStack.push(fc.returnValueAdr);
                ssType.push(1);
                //SymbolTable.progLine++;
                table.newScope();
                break;
            case "UPDATE":
                ssType.pop();
                newInp= table.findByMemoryAdress(semanticStack.pop()).token;
                fc=((FunctionCell)table.findByRetAddr(semanticStack.peek()));
                fc.newIntInput(table, newInp);
                break;
            case "UPDATEARR":
                ssType.pop();
                newInp= table.findByMemoryAdress(semanticStack.pop()).token;
                fc=((FunctionCell)table.findByRetAddr(semanticStack.peek()));
                fc.newArrayInput(table, newInp);
                break;
            case "START":
                fc=((FunctionCell)table.findByRetAddr(semanticStack.peek()));
                fc.startingAdr=PB.size();
                break;
            case "SET":
                fc=((FunctionCell)table.findByRetAddr(semanticStack.peek()));
                if (fc == null) {
                    ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, nextToken,
                            "Identifier " + table.findByMemoryAdress(semanticStack.peek()).token.getLexeme() + " is not a function");
                    semanticStack.push(0);
                    ssType.push(0);
                } else {
                    inpSize = fc.inputNum;
                    semanticStack.push(inpSize);
                    ssType.push(inpSize);
//                System.out.println("INPUT size: "+inpSize);
                }
                break;
            case "FUNCINP":
                op1=semanticStack.pop();
                type1=ssType.pop();
                inpSize=semanticStack.pop();
                inpSize--;
                fc=((FunctionCell)table.findByRetAddr(semanticStack.peek()));
//                System.out.println("INPUT size: "+inpSize);
                if (inpSize < 0) {
                    ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, nextToken,
                            "when calling " + fc.token.getLexeme() + " the number of provided arguments is greater than expected. ignoring excessive args");
                    semanticStack.push(0);
                } else {
                    Cell inp = fc.allInputs.get(inpSize);
                    c = table.findByMemoryAdress(op1);
                    if (fc.byValue.get(inpSize)) {
                        if (c != null && c.cellType != Cell.Type.Int)
                            ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, nextToken,
                                    "Expected value for arg " + fc.allInputs.get(inpSize).token.getLexeme() + ". but got an array " + c.token.getLexeme());
                        else
                            PB.add("(ASSIGN, " + signedPrint(op1, type1) + "," + inp.getMemAdr() + ")");
                    } else {
                        if (c == null || (c.cellType != Cell.Type.Array && c.cellType != Cell.Type.DynamicArray))
                            ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, nextToken,
                                    "Expected array reference for arg " + fc.allInputs.get(inpSize).token.getLexeme() + ". but got a value ");
                        else
                            PB.add("(ASSIGN, " + signedPrint(op1, (c.cellType == Cell.Type.Array ? 2 : 3))
                                    + "," + inp.getMemAdr() + ")");
                    }
                    semanticStack.push(inpSize);
                }
                break;
            case "JPFUNC":
                inpSize=semanticStack.pop(); ssType.pop();
                fc=((FunctionCell)table.findByRetAddr(semanticStack.peek()));
                if (fc == null)
                    break;
                PB.add("(ASSIGN, "+ signedPrint(PB.size()+2, 2)+", "+fc.returnAdr+")"); //Assign Return Address
                if(inpSize==0){
                    //fc.returnAdr=PB.size();
                    //System.out.println("set ret addr to " + fc.returnAdr);
                }else{
                    ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, nextToken,
                            "when calling " + fc.token.getLexeme() + " the number of provided arguments is less than expected");
                }
                PB.add("(JP, "+fc.startingAdr+")");
                break;
            case "INTRET":
                op1=semanticStack.pop();
                type1=ssType.pop();
                fc= table.currentFunc;
                if(fc.retType==FunctionCell.returnType.Int){
                    PB.add("(ASSIGN, "+signedPrint(op1, type1)+","+fc.returnValueAdr+")");
                    /*if(fc.token.getLexeme().equals( "main")){
                        PB.add("");
                        semanticStack.push(PB.size()-1);
                        ssType.push(1);
                    }*/
                }else{
                    ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, nextToken,
                            "void function " + fc.token.getLexeme() + " cannot return a value");
                }
                PB.add("(JP,"+signedPrint(fc.returnAdr, 3)+")");
                break;
            case "VOIDRET":
                fc= table.currentFunc;
                if(fc.retType!=FunctionCell.returnType.Void){
                    ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, nextToken,
                            "Missing function " + fc.token.getLexeme() + " return value");
                }
                PB.add("(JP,"+signedPrint(fc.returnAdr, 3)+")");
                break;
            case "VOIDMATCH":
                //System.out.println("Input size is " + semanticStack.peek());
                break;
            case "EOF":
                fc = (FunctionCell)table.findByLexeme("main");
                if (fc == null)
                    ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, "main is not defined.");
                else if (fc.retType != FunctionCell.returnType.Void)
                    ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, "main() signature does not match. Return type must be void.");
                else if (fc.allInputs.size() > 0)
                    ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, "main() signature does not match. Input arguments must be void");
                else
                    PB.set(0, "(ASSIGN, #" + (PB.size() - 1) + "," + ((FunctionCell) table.findByLexeme("main")).returnAdr + ")");
                break;

            case "CHECKID":
                c = table.findByMemoryAdress(semanticStack.peek());
                if (c == null)
                    c = table.findByRetAddr(semanticStack.peek());
                //if (c.cellType == Cell.Type.Array || c.cellType == Cell.Type.DynamicArray)
                    //ErrorLogger.printError(ErrorLogger., "Identifier " + c.token.getLexeme() + " is an array and must be used with []");
                else if (c.cellType == Cell.Type.Function)
                    ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, "Identifier " + c.token.getLexeme() + " is a function and must be used with ()");
                break;

            case "CHECKFUNC":
                fc = (FunctionCell)table.findByRetAddr(semanticStack.peek());
                if (fc != null && fc.retType == FunctionCell.returnType.Void)
                    ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, nextToken,
                            "Cannot use void function " + fc.token.getLexeme() + " in expression");
                break;

            case "FUNEND":
                fc= table.currentFunc;
                PB.add("(JP,"+signedPrint(fc.returnAdr, 3)+")");
                this.TACgenerate("BLCOKE", nextToken);
                break;
        }
    }
/*    private int rightValue(int op1, int type1){
        switch (type1){
            case 1: return SymbolTable.Memory[op1/4];
            case 2: return op1;
            case 3: return SymbolTable.Memory[SymbolTable.Memory[op1/4]/4];
            default: return 0;
        }
    }*/
    private String signedPrint(int adr, int type){
        switch (type){
            case 1: return String.valueOf(adr);
            case 2: return "#"+adr;
            case 3: return "@"+adr;
            default: return "0";
        }
    }
    @Override
    public String toString() {
        String res="Program Block:\n";
        for(int i=0; i<PB.size(); i++){
            res+=i + ": " + PB.get(i)+"\n";
        }
        return res;
    }
    private int getTemp(){
        tempMem+=4;
        return tempMem-4;
    }
}
