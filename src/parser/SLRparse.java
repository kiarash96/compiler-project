package parser;

import lexical.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by Yeganeh on 6/11/2017.
 */

public class SLRparse {
    private Stack st = new Stack();
    private ParseTable table= new ParseTable();
    lexical.Scanner scanner = new lexical.Scanner(new FileInputStream("test.txt"));

    public SLRparse() throws FileNotFoundException {
    }

    public void parse() throws IOException {
        int row, col;
        st.push(0);
        boolean flag=false; //get new token or not
        Token nextToken=scanner.getNextToken();
        while(true){
            if(flag)
                nextToken= scanner.getNextToken();

            col=Arrays.asList(table.actionTableHead).indexOf(nextToken.getSpecificType());
            row= (Integer) st.peek();

            String action= table.actionTable[row][col];

            if(action.equals("acc")){
                System.out.println("accept");
                break;
            }
            if(action.isEmpty()){
                System.out.println("ERROR: wrong input, Ignore it until correct input");
                while (true){
                    int state=(Integer)st.peek();
                    List<Integer> ans= new ArrayList<>();

                    for(int i=0; i<table.gotoTable[state].length; i++)
                    {
                        if(table.gotoTable[state][i]!=0)
                            ans.add(i);
                    }
                    if(!ans.isEmpty()){
//                        System.out.println(state);
                        boolean handled=false;
                        while(!handled){
                            Token next= scanner.getNextToken();
//                            System.out.println(next.getSpecificType());
                            for(int i=0; i<ans.size();i++){
                                if(Arrays.asList(table.follows[ans.get(i)]).indexOf(next.getSpecificType()) != -1){
                                    handled=true;
                                    nextToken=next;
                                    st.push(table.gotoHead[ans.get(i)]);
                                    st.push(table.gotoTable[state][ans.get(i)]);
                                    System.out.println(st);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    else{
                        System.out.println("");
                        pop(2,st);
                    }
                }
                flag=false;
                continue;
            }
            switch (action.charAt(0)){
                case 's':
                    flag=true;
                    action=action.replace("s","");
                    int state= Integer.parseInt(action);
                    st.push(nextToken.getSpecificType());
                    st.push(state);
                    System.out.println(st);
                    break;
                case 'r':
                    flag=false;
                    action=action.replace("r","");
                    int redgrammar= Integer.parseInt(action);
                    String LHS= table.grammar[redgrammar-1][0];
                    int RHS= Integer.parseInt(table.grammar[redgrammar-1][1]);
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


    private void pop(int num, Stack st){
        for(int i=0; i< num; i++)
            st.pop();
    }
}

