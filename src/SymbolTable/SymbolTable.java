package SymbolTable;

import lexical.IDToken;
import lexical.Token;
import java.util.*;
import SymbolTable.Cell;
/**
 * Created by User on 6/11/2017.
 */
public class SymbolTable {
    public static int progLine=0;
    public static int memLine=0;
    public List<Cell> table=new ArrayList<>();
    private static Stack<Integer> scopeStack= new Stack<>();

    public Cell addToTable(Token token){
        int a=2;
        if(token.getTokenType()==Token.TokenType.ID){

            if(contains((IDToken)token)){
                // TODO Error handling. id declared for second time
                System.out.println("ERROR: ID has been declared before");
            }
            else{
                Cell n= new Cell((IDToken) token, progLine,scopeStack.peek() ,memLine);
                memLine+=4;
                table.add(n);
                return n;
            }
        }else{
            System.out.println("ERROR: invalid ID name. ID name can not be keyword or symbol");
            //TODO error handle: hazf konim in khato ?
        }
        return null;
    }

    public boolean contains(IDToken token){
        for (int i=0; i< table.size(); i++){
            IDToken tblToken=table.get(i).token;
            if(tblToken.getLexeme().equals(token.getLexeme())){
                if(scopeStack.contains(table.get(i).scope))
                    return true;
            }
        }
        return false;
    }
}