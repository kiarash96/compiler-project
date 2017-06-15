package parser.generator;

import lexical.KeywordToken;
import lexical.SymbolToken;
import lexical.Token;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by kiarash on 6/13/17.
 */
public class SLRTableGenerator {
    List<Production> grammar;
    Map<String, Boolean> isTerminal;
    List<String> terminals, nonterminals;
    Map<String, List<String>> follows;
    Map<String, Object> lexemeToTokenType;

    public SLRTableGenerator(String grammarFile, String followsFile) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(grammarFile));

        grammar = new ArrayList<>();
        isTerminal = new LinkedHashMap<>();

        String lhs = scanner.next();
        scanner.next(); // ->
        ArrayList<String> rhs = new ArrayList<>();

        while (scanner.hasNext()) {
            String next = scanner.next();
            if (next.equals("|")) {
                grammar.add(new Production(lhs, rhs));
                rhs.clear();
            }
            else if (next.equals("->")) {
                String nextLhs = rhs.get(rhs.size() - 1);
                rhs.remove(rhs.size() - 1);
                grammar.add(new Production(lhs, rhs));
                lhs = nextLhs;
                rhs.clear();
            }
            else
                rhs.add(next);
        }
        grammar.add(new Production(lhs, rhs));

        /*for (Production p : grammar)
            System.out.println(p);
        System.out.println();*/

        // set isTerminal
        for (Production p : grammar) {
            isTerminal.put(p.lhs, false);
            for (String x : p.rhs)
                if (!isTerminal.containsKey(x))
                    isTerminal.put(x, true);
        }

        terminals = new ArrayList<>();
        nonterminals = new ArrayList<>();
        for (String x : isTerminal.keySet())
            if (isTerminal.get(x))
                terminals.add(x);
            else
                nonterminals.add(x);
        terminals.add("eof");

        /*for (String str : isTerminal.keySet())
            System.out.println(str + " is " + (isTerminal.get(str) ? "terminal" : "non-terminal"));
        System.out.println();*/

        lexemeToTokenType = new HashMap<>();
        lexemeToTokenType.put("id", Token.TokenType.ID);
        lexemeToTokenType.put("num", Token.TokenType.NUMBER);
        lexemeToTokenType.put("eof", Token.TokenType.EOF);
        lexemeToTokenType.put("int", KeywordToken.KeywordType.INT);
        lexemeToTokenType.put("void", KeywordToken.KeywordType.VOID);
        lexemeToTokenType.put("if", KeywordToken.KeywordType.IF);
        lexemeToTokenType.put("else", KeywordToken.KeywordType.ELSE);
        lexemeToTokenType.put("while", KeywordToken.KeywordType.WHILE);
        lexemeToTokenType.put("return", KeywordToken.KeywordType.RETURN);
        lexemeToTokenType.put(";", SymbolToken.SymbolType.SEMICOLON);
        lexemeToTokenType.put(",", SymbolToken.SymbolType.COMMA);
        lexemeToTokenType.put("[", SymbolToken.SymbolType.OPEN_BRACKET);
        lexemeToTokenType.put("]", SymbolToken.SymbolType.CLOSE_BRACKET);
        lexemeToTokenType.put("{", SymbolToken.SymbolType.OPEN_CURLY_BRACES);
        lexemeToTokenType.put("}", SymbolToken.SymbolType.CLOSE_CURLY_BRACES);
        lexemeToTokenType.put("(", SymbolToken.SymbolType.OPEN_PARENTHESIS);
        lexemeToTokenType.put(")", SymbolToken.SymbolType.CLOSE_PARENTHESIS);
        lexemeToTokenType.put("=", SymbolToken.SymbolType.ASSIGNMENT);
        lexemeToTokenType.put("&&", SymbolToken.SymbolType.AND_AND);
        lexemeToTokenType.put("==", SymbolToken.SymbolType.EQUALITY);
        lexemeToTokenType.put("<", SymbolToken.SymbolType.LESSTHAN);
        lexemeToTokenType.put("+", SymbolToken.SymbolType.PLUS);
        lexemeToTokenType.put("-", SymbolToken.SymbolType.MINUS);
        lexemeToTokenType.put("*", SymbolToken.SymbolType.STAR);
        lexemeToTokenType.put("/", SymbolToken.SymbolType.SLASH);

        // read follows.txt
        scanner = new Scanner(new FileInputStream(followsFile));
        follows = new HashMap<>();
        while (scanner.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(scanner.nextLine(), " ");
            String nonterm = st.nextToken();
            nonterm = nonterm.substring(0, nonterm.length() - 1);
            List<String> followList = new ArrayList<>();
            while (st.hasMoreTokens())
                followList.add(st.nextToken());
            follows.put(nonterm, followList);
        }
    }

    public ParseTable generate() {
        List<State> states = new ArrayList<>();
        states.add(createInitialState());

        while (true) {
            Queue<State> toAdd = new LinkedList<>();

            for (State s : states) {
                Set<String> nextSymbols = new LinkedHashSet<>();
                for (Item i : s.items)
                    if (!i.isReduce())
                        nextSymbols.add(i.nextSymbol());

                for (String x : nextSymbols)
                    toAdd.add(s.nextState(grammar, x));
            }

            boolean ended = true;
            for (State s : toAdd)
                if (!states.contains(s)) {
                    states.add(s);
                    ended = false;
                }

            if (ended)
                break;
        }

        /*for (int i = 0; i < states.size(); i ++)
            System.out.println("s" + i + ":\n" + states.get(i));*/

        ParseTable table = new ParseTable();
        table.follows = new Object[nonterminals.size()][];

        table.gotoHead = new String[nonterminals.size()];
        nonterminals.toArray(table.gotoHead);

        table.gotoTable = new int[states.size()][nonterminals.size()];

        table.actionTableHead = new Object[terminals.size()];
        for (int i = 0; i < terminals.size(); i ++)
            table.actionTableHead[i] = lexemeToTokenType.get(terminals.get(i));

        table.actionTable = new String[states.size()][terminals.size()];
        for (int i = 0; i < table.actionTable.length; i ++)
            for (int j = 0; j < table.actionTable[i].length; j ++)
                table.actionTable[i][j] = "";

        for (int myindex = 0; myindex < states.size(); myindex ++) {
            State s = states.get(myindex);

            Set<String> nextSymbols = new LinkedHashSet<>();
            for (Item i : s.items)
                if (!i.isReduce())
                    nextSymbols.add(i.nextSymbol());

            for (Item i : s.items)
                if (i.isReduce())
                    for (String t : follows.get(i.p.lhs))
                        table.actionTable[myindex][terminals.indexOf(t)] = "r" + grammar.indexOf(i.p);

            for (String x : nextSymbols) {
                State nextState = s.nextState(grammar, x);
                int nextIndex = states.indexOf(nextState);

                if (isTerminal.get(x))
                    table.actionTable[myindex][terminals.indexOf(x)] = "s" + nextIndex;
                else
                    table.gotoTable[myindex][nonterminals.indexOf(x)] = nextIndex;
            }
        }

        for (String nonterm : follows.keySet()) {
            int ntindex = nonterminals.indexOf(nonterm);
            table.follows[ntindex] = new Object[follows.get(nonterm).size()];
            for (int i = 0; i < follows.get(nonterm).size(); i++)
                table.follows[ntindex][i] = lexemeToTokenType.get(follows.get(nonterm).get(i));
        }

        table.grammar = new String[grammar.size()][2];
        for (int i = 0; i < grammar.size(); i ++) {
            table.grammar[i][0] = grammar.get(i).lhs;
            table.grammar[i][1] = String.valueOf(grammar.get(i).rhs.length);
        }

        return table;
    }

    private State createInitialState() {
        String startSymbol = grammar.get(0).lhs;
        Set<Item> itemSet = new LinkedHashSet<>();
        for (Production p : grammar)
            if (p.lhs.equals(startSymbol))
                itemSet.add(new Item(p, 0));
        return new State(grammar, itemSet);
    }
}
