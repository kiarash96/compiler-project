package SymbolTable;

import errors.ErrorLogger;
import lexical.IDToken;
import lexical.Token;
import java.util.*;
import SymbolTable.Cell;
/**
 * Created by User on 6/11/2017.
 */
public class SymbolTable {
//    public static int[] Memory= new int [1000];
    public static int progLine=0;
    public static int memLine=0;
    public List<Cell> table=new ArrayList<>();
    private static Stack<Integer> scopeStack= new Stack<>();

    public FunctionCell currentFunc = null;

    public SymbolTable(){
        scopeStack.add(0);
    }

    public FunctionCell funcAddToTable(int adr, FunctionCell.returnType r){ // tabe ha to memory zakhire nemishan dge?
        Cell c=removeByAdress(adr);
        FunctionCell cell= new FunctionCell(c.token,progLine, scopeStack.peek(), -1, r);
        cell.cellType= Cell.Type.Function;
//        memLine+=1;
        table.add(cell);
        return cell;
    }
    public Cell addToTable(Token token){
        int a=2;
        if(token.getTokenType()==Token.TokenType.ID){

            if(containsThisScope((IDToken)token)){
                ErrorLogger.printError(ErrorLogger.SEMANTIC_ERROR, token, "ID has been declared before in this scope!");
                return findByLexeme(token.getLexeme());
            }
            else{
                Cell n= new Cell((IDToken) token, progLine,scopeStack.peek() ,4*memLine);
                memLine+=1;
                table.add(n);
                return n;
            }
        }else{
            System.out.println("ERROR: invalid ID name. ID name can not be keyword or symbol");
        }
        return null;
    }

    public boolean containsThisScope(IDToken token){
        for (int i=0; i< table.size(); i++){
            IDToken tblToken=table.get(i).token;
            if(tblToken.getLexeme().equals(token.getLexeme())){
                if(scopeStack.peek()==table.get(i).scope ||
                        (scopeStack.peek() == table.get(i).scope + 1
                        && table.get(i).isArg))
                    return true;
            }
        }
        return false;
    }
    public Cell findByLexeme(String s){
        for( int i= table.size()-1; i>=0; i--){ // ba farze inke scope haye faal faghat hastand neveshte shode
            IDToken tblToken=table.get(i).token;
            if(tblToken.getLexeme().equals(s))
            {
                return table.get(i);
            }
        }
        return null;
    }
    public Cell removeByAdress(int adr){
        for( int i= table.size()-1; i>=0; i--){
            if(table.get(i).getMemAdr()==adr)
            {
                Cell c=table.get(i);
                table.remove(i);
                memLine-=1;
                return c;
            }
        }
        return null;
    }
    public Cell findByMemoryAdress(int adr){
        Cell c;
        for (int i=0; i< table.size(); i++){
            c=table.get(i);
            if(c.getMemAdr()==adr){
                return c;
            }
        }
        return null;
    }
    public Cell findByLine(int l){
        Cell c;
        for (int i=0; i< table.size(); i++){
            c=table.get(i);
            if(c.getLine()==l){
                return c;
            }
        }
        return null;
    }
    public int newScope(){
        scopeStack.push(progLine);
        return progLine;
    }
    public void deleteScope(){
        int lastScope=scopeStack.pop();
        for( int i= table.size()-1; i>=0; i--){
            Cell tblToken=table.get(i);
            if(tblToken.scope == lastScope)
            {
                table.remove(i);
//                memLine-=1;
            }
            else{
                return;
            }
        }
    }

    @Override
    public String toString() {
        String res = "Symbol Table:";
        for( int i=0; i<table.size(); i++){
            res+=i+"th row: "+table.get(i).toString()+"\n";
        }
        return res;
    }
}
