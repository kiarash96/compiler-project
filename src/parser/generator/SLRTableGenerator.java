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
    List<String> terminals, nonterminals;
    Map<String, Set<String>> firsts, follows;
    Map<String, Object> lexemeToTokenType;

    public SLRTableGenerator(String grammarFile) throws FileNotFoundException, ParserGeneratorException {
        Scanner scanner = new Scanner(new FileInputStream(grammarFile));

        grammar = new ArrayList<>();

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

        // detect terminals and nonterminals
        Map<String, Boolean> isTerminal = new LinkedHashMap<>();
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
        lexemeToTokenType.put("output", KeywordToken.KeywordType.OUTPUT);
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

        // check grammar.txt terminals integrity
        for (String x : terminals)
            if (!lexemeToTokenType.containsKey(x))
                throw new ParserGeneratorException("Terminal " + x + " defined in grammar.txt is unknown.");


        calculateFirsts();
        calculateFollows();
        /*for (String nonterm : firsts.keySet()) {
            System.out.print("First(" + nonterm + ") = {");
            for (String x : firsts.get(nonterm))
                System.out.print("\"" + x + "\", ");
            System.out.println("}");
        }*/
        /*for (String nonterm : follows.keySet()) {
            System.out.print("Follow(" + nonterm + ") = {");
            for (String x : follows.get(nonterm))
                System.out.print("\"" + x + "\", ");
            System.out.println("}");
        }*/

        for (String nonterm : nonterminals)
            if (follows.get(nonterm).isEmpty())
                throw new ParserGeneratorException("Non-terminal " + nonterm + " is not reachable.");
    }

    public ParseTable generate() throws ParserGeneratorException {
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
                if (i.isReduce()) {
                    for (String t : follows.get(i.p.lhs)) {
                        String action = "";
                        if (grammar.indexOf(i.p) == 0)
                            action = "acc";
                        else
                            action = "r" + grammar.indexOf(i.p);
                        setAction(table, myindex, t, action);
                    }
                }

            for (String x : nextSymbols) {
                State nextState = s.nextState(grammar, x);
                int nextIndex = states.indexOf(nextState);

                if (terminals.contains(x))
                    setAction(table, myindex, x, "s" + nextIndex);
                else
                    table.gotoTable[myindex][nonterminals.indexOf(x)] = nextIndex;
            }
        }

        // fill table.follows
        for (String nonterm : follows.keySet()) {
            int ntindex = nonterminals.indexOf(nonterm);
            table.follows[ntindex] = new Object[follows.get(nonterm).size()];
            int i = 0;
            for (String term : follows.get(nonterm))
                table.follows[ntindex][i ++] = lexemeToTokenType.get(term);
        }

        // fill table.grammar
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

    private void setAction(ParseTable table, int sindex, String term, String action) throws ParserGeneratorException {
        String currentAction = table.actionTable[sindex][terminals.indexOf(term)];
        String finalAction = action;

        // conflicts
        if (!(currentAction.equals("") || currentAction.equals(action))) {
            if (sindex == 135 && term.equals("else"))
                finalAction = "s139";
            else {
                throw new ParserGeneratorException("Conflict in [s" + sindex + ", " + term + "]"
                        + " between " + action + " and "
                        + currentAction);
            }
        }

        table.actionTable[sindex][terminals.indexOf(term)] = finalAction;
    }

    private void calculateFirsts() {
        firsts = new LinkedHashMap<>();
        for (String nonterm : nonterminals)
            firsts.put(nonterm, new HashSet<>());

        for (Production p : grammar) {
            if (p.isEpsilon())
                firsts.get(p.lhs).add("''");
            else if (terminals.contains(p.rhs[0]))
                firsts.get(p.lhs).add(p.rhs[0]);
        }

        boolean isChanged;
        do {
            isChanged = false;

            for (Production p : grammar) {
                Queue<String> addQ = new LinkedList<>();
                boolean didBreak = false;
                for (String rhs : p.rhs)
                    if (terminals.contains(rhs)) {
                        addQ.add(rhs);
                        didBreak = true;
                        break;
                    } else { // non-terminal
                        for (String x : firsts.get(rhs))
                            if (!x.equals("''"))
                                addQ.add(x);
                        if (!firsts.get(rhs).contains("''")) {
                            didBreak = true;
                            break;
                        }
                    }

                if (!didBreak)
                    addQ.add("''");

                for (String x : addQ)
                    if (!firsts.get(p.lhs).contains(x)) {
                        isChanged = true;
                        firsts.get(p.lhs).add(x);
                    }
            }

        } while (isChanged);
    }

    private void calculateFollows() {
        follows = new LinkedHashMap<>();
        for (String nonterm : nonterminals)
            follows.put(nonterm, new HashSet<>());

        for (Production p : grammar)
            for (int i = 1; i < p.rhs.length; i ++)
                if (terminals.contains(p.rhs[i]) && nonterminals.contains(p.rhs[i - 1]))
                    follows.get(p.rhs[i - 1]).add(p.rhs[i]);
        follows.get(grammar.get(0).lhs).add("eof");

        boolean isChanged;
        do {
            isChanged = false;

            for (Production p : grammar)
                for (int i = 0; i < p.rhs.length; i ++) {
                    if (!nonterminals.contains(p.rhs[i]))
                        continue;

                    boolean didBreak = false;
                    for (int j = i + 1; j < p.rhs.length; j++) {
                        if (terminals.contains(p.rhs[j])) {
                            isChanged |= addAllToFollows(p.rhs[i], new HashSet<>(Arrays.asList(p.rhs[j])));
                            didBreak = true;
                            break;
                        } else { // non-terminal
                            isChanged |= addAllToFollows(p.rhs[i], firsts.get(p.rhs[j]));
                            if (!firsts.get(p.rhs[j]).contains("''")) {
                                didBreak = true;
                                break;
                            }
                        }
                    }

                    if (!didBreak)
                        isChanged |= addAllToFollows(p.rhs[i], follows.get(p.lhs));
                }
        } while (isChanged);
    }

    private boolean addAllToFollows(String nonterm, Set<String> s) {
        boolean addedNew = false;
        for (String x : s)
            if (!x.equals("''") && !follows.get(nonterm).contains(x)) {
                addedNew = true;
                follows.get(nonterm).add(x);
            }
       return addedNew;
    }
}
