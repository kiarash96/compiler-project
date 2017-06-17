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
    public CodeGenerator(){
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
                // tarif shodane ghablesh check beshe
                Cell id=table.findByLexeme(((IDToken)nextToken).getLexeme());
                if(id.getMemAdr()!=-1)
                    semanticStack.push(id.getMemAdr());
                else
                    semanticStack.push(id.getLine());
                ssType.push(1);
                break;
            case "PARR":
                int index=semanticStack.pop();
                int base=semanticStack.pop();
                ssType.pop();ssType.pop();
                c=table.findByMemoryAdress(base);

                if(c.cellType.equals(Cell.Type.DynamicArray)){
                    System.out.println("PARR special case");
                    t=getTemp();
                    PB.add("(ADD, "+signedPrint(base,3)+","+(index*4)+", "+t+")");
                    semanticStack.push(t);
                    ssType.push(3);
                }else{
                    adr=base+4*index;
                    semanticStack.push(adr);
                    ssType.push(1);
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
                Cell mainFunc=table.findByLine(main);
                if(mainFunc.token.getLexeme().equals("main")){
                    PB.set(0, "(JP, "+PB.size()+")");
                }
                break;
            case "PRINT":
                //TODO ERROR HANDLING: check if ss[top] is a number it can be void function!!!
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
                semanticStack.push(fc.getLine());
                ssType.push(1);
                SymbolTable.progLine++;
                table.newScope();
                break;
            case "VOIDFUNC":
                adr =semanticStack.pop();
                ssType.pop();
                fc = table.funcAddToTable(adr, FunctionCell.returnType.Void);
                semanticStack.push(fc.getLine());
                ssType.push(1);
                SymbolTable.progLine++;
                table.newScope();
                break;
            case "UPDATE":
                ssType.pop();
                newInp= table.findByMemoryAdress(semanticStack.pop()).token;
                fc=((FunctionCell)table.findByLine(semanticStack.peek()));
                fc.newIntInput(table, newInp);
                break;
            case "UPDATEARR":
                ssType.pop();
                newInp= table.findByMemoryAdress(semanticStack.pop()).token;
                fc=((FunctionCell)table.findByLine(semanticStack.peek()));
                fc.newArrayInput(table, newInp);
                break;
            case "START":
                fc=((FunctionCell)table.findByLine(semanticStack.peek()));
                fc.startingAdr=PB.size();
                break;
            case "SET":
                fc=((FunctionCell)table.findByLine(semanticStack.peek()));
                inpSize=fc.inputNum;
                semanticStack.push(inpSize);
                ssType.push(inpSize);
//                System.out.println("INPUT size: "+inpSize);
                break;
            case "FUNCINP":
                op1=semanticStack.pop();
                type1=ssType.pop();
                inpSize=semanticStack.pop();
                inpSize--;
                fc=((FunctionCell)table.findByLine(semanticStack.peek()));
//                System.out.println("INPUT size: "+inpSize);
                Cell inp=fc.allInputs.get(inpSize);
                if(fc.byValue.get(inpSize)){ //TODO check konim noe vorudi ha ba int o array tabe mikhune ya na
                    PB.add("(ASSIGN, "+signedPrint(op1,type1)+","+inp.getMemAdr()+")");
                }else{
                    PB.add("(ASSIGN, "+signedPrint(op1,2)+","+inp.getMemAdr()+")");
                }
                semanticStack.push(inpSize);
                break;
            case "JPFUNC":
                inpSize=semanticStack.pop(); ssType.pop();
                fc=((FunctionCell)table.findByLine(semanticStack.peek()));
                PB.add("(ASSIGN, "+ signedPrint(PB.size()+2, 2)+", "+fc.returnAdr+")"); //Assign Return Address
                if(inpSize==0){
                    //fc.returnAdr=PB.size();
                    //System.out.println("set ret addr to " + fc.returnAdr);
                    PB.add("(JP, "+fc.startingAdr+")");
                }else{
                    //TODO number of inputs does not match function params
                }
                break;
            case "INTRET": //TODO return type mathch has ba function return Type
                op1=semanticStack.pop();
                type1=ssType.pop();
                fc=((FunctionCell)table.findByLine(semanticStack.pop()));
                PB.add("(ASSIGN, "+signedPrint(op1, type1)+","+fc.returnValueAdr+")");
                ssType.pop();
                if(fc.retType==FunctionCell.returnType.Int){
                    if(fc.token.getLexeme().equals( "main")){
                        PB.add("");
                        semanticStack.push(PB.size()-1);
                        ssType.push(1);
                    }
                    PB.add("(JP,"+signedPrint(fc.returnAdr, 3)+")");
                }else{
                    //Err
                }
                break;
            case "VOIDRET":
                fc=((FunctionCell)table.findByLine(semanticStack.pop()));
                ssType.pop();
                if(fc.retType==FunctionCell.returnType.Void){
                    PB.add("(JP,"+signedPrint(fc.returnAdr, 3)+")");
                }else{
                    //Err
                }
                break;
            case "VOIDMATCH":
                //TODO vaghti inja miad ke to call vorudi hichi nade. check konim vaghean tabe void bashe vorudish
                break;
            case "EOF":
                op1=semanticStack.pop();
                ssType.pop();
                PB.set(op1, "(ASSIGN, "+PB.size()+","+((FunctionCell)table.findByLexeme("main")).returnAdr+")");
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
