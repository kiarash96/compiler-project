package SymbolTable;

import lexical.IDToken;
import parser.CodeGenerator;

import java.util.*;

/**
 * Created by User on 6/15/2017.
 */
public class FunctionCell extends Cell {
    public enum returnType{
        Void,
        Int
    }
    public List<Cell> allInputs=new ArrayList<Cell>();
    public List<Boolean> byValue= new ArrayList<>(); // if i is true then it is int, if false it is array
    public returnType retType= returnType.Int;
    public int inputNum=0; // tedad vorudi haye tabe
    public int startingAdr;
    public int returnAdr;
    public int returnValueAdr;

    public FunctionCell(IDToken token, int scope, int memoryAddress, returnType r) {
        super(token, scope, memoryAddress);
        this.retType=r;
        returnValueAdr=SymbolTable.memLine*4;
        SymbolTable.memLine++;
        returnAdr=SymbolTable.memLine*4;
        SymbolTable.memLine++;
    }

    public void newIntInput(SymbolTable tbl, IDToken token){
        inputNum++;
        Cell c=tbl.findByLexeme(token.getLexeme());
        c.isArg = true;
        allInputs.add(0,c);
        byValue.add(0,true);
    };
    public void newArrayInput(SymbolTable tbl, IDToken token){
        inputNum++;
        Cell c=tbl.findByLexeme(token.getLexeme());
        c.isArg = true;
        allInputs.add(0,c);
        byValue.add(0,false);
        c.cellType= Type.DynamicArray;
    };

    public String toString() {
        String res=super.toString();
        res+=byValue;
        return res;
    }
}
