package SymbolTable;

import lexical.IDToken;


public class Cell {
    public IDToken token;
    private int occurredLine;
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

    public Cell(IDToken token, int line, int scope, int memoryAddress){
        this.token=token;
        this.occurredLine=line;
        this.scope=scope;
        this.memoryAddress=memoryAddress;
    }
    public int getMemAdr(){
        return memoryAddress;
    }
    public int getLine() {return occurredLine;}
    @Override
    public String toString() {
        String res="";
        res+="NAME:"+ token.getLexeme()+", Scope: "+scope+", Memory Address: "+memoryAddress+", Line declared: "+occurredLine;
        return res;
    }

}
