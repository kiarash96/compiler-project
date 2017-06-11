package parser;

import lexical.IDToken;

import static parser.Cell.Type.Int;

public class Cell{
    public IDToken token;
    private int occurredLine;
    public int scope;
    private int memoryAddress;
    public enum Type{
        Array,
        Function,
        Int
    }
    public Type cellType=Int;

    public Cell(IDToken token, int line, int scope, int memoryAddress){
        this.token=token;
        this.occurredLine=line;
        this.scope=scope;
        this.memoryAddress=memoryAddress;
    }
}
