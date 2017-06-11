package parser;

import java.util.*;

/**
 * Created by Yeganeh on 6/11/2017.
 */

public class SLRparse {
    private String[] inp={"id","*", "(","id" ,"+", "id",")", "$"};
    private Stack st = new Stack();
    private void pop(int num, Stack st){
        for(int i=0; i< num; i++)
            st.pop();
    }
    public void parse(){
        int index=0;
        ParseTable table= new ParseTable();
        st.push(0);
        while(true){
            String next=inp[index];
//            System.out.println(next);
            int col=Arrays.asList(table.symbols).indexOf(next);
            int row= (Integer) st.peek();
//            System.out.println("Row "+row+" Col "+ col);
            String action= table.actionTable[row][col];
            if(action.equals("acc")){
                System.out.println("accept");
                break;
            }
            switch (action.charAt(0)){
                case 's':
                    action=action.replace("s","");
                    int state= Integer.parseInt(action);
                    st.push(next);
                    st.push(state);
                    System.out.println(st);
                    index++;
                    break;
                case 'r':
                    action=action.replace("r","");
                    int redgrammar= Integer.parseInt(action);
//                    System.out.println(redgrammar);
                    String LHS= table.grammar[redgrammar-1][0];
                    int RHS= Integer.parseInt(table.grammar[redgrammar-1][1]);
//                    System.out.println("RHS"+RHS);
//                    System.out.println("LHS"+LHS);
                    pop(2*RHS,st);
                    int gotoRow=(Integer)st.peek();
                    st.push(LHS);
                    int gotoCol=Arrays.asList(table.gotoHead).indexOf(LHS);
                    st.push(table.gotoTable[gotoRow][gotoCol]);
                    System.out.println(st);
                    break;
            }
        }
    }
}

