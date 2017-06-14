package parser.generator;

import parser.ParseTable;

import java.util.*;

/**
 * Created by kiarash on 6/13/17.
 */
public class SLRTableGenerator {
    List<Production> grammar;
    Map<String, Boolean> isTerminal;

    public SLRTableGenerator(Scanner scanner) {
        grammar = new ArrayList<>();
        isTerminal = new HashMap<>();

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

        for (Production p : grammar)
            System.out.println(p);
        System.out.println();

        // set isTerminal
        for (Production p : grammar) {
            isTerminal.put(p.lhs, false);
            for (String x : p.rhs)
                if (!isTerminal.containsKey(x))
                    isTerminal.put(x, true);
        }

        for (String str : isTerminal.keySet())
            System.out.println(str + " is " + (isTerminal.get(str) ? "terminal" : "non-terminal"));
        System.out.println();
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

        for (int i = 0; i < states.size(); i ++)
            System.out.println("s" + i + ":\n" + states.get(i));

        return new ParseTable();
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
