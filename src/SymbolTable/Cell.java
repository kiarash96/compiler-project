package SymbolTable;

import lexical.IDToken;


public class Cell {
    public IDToken token;
    public int scope;
    public boolean isArg = false;
    public int size=1;
    private int memoryAddress;
    public enum Type{
        Array,
        Function,
        Int,
        DynamicArray
    }
    public Type cellType=Cell.Type.Int;

    public Cell(IDToken token, int scope, int memoryAddress){
        this.token=token;
        this.scope=scope;
        this.memoryAddress=memoryAddress;
    }
    public int getMemAdr(){
        return memoryAddress;
    }
    @Override
    public String toString() {
        String res="";
        res+="NAME:"+ token.getLexeme()+", Scope: "+scope+", Memory Address: "+memoryAddress;
        return res;
    }

}
