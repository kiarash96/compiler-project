package parser.generator;

import parser.ParseTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by kiarash on 6/13/17.
 */
public class SLRTableGenerator {
    ArrayList<Production> grammar;
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

        // set isTerminal
        for (Production p : grammar) {
            isTerminal.put(p.lhs, false);
            for (String x : p.rhs)
                if (!isTerminal.containsKey(x))
                    isTerminal.put(x, true);
        }
    }

    public ParseTable generate() {

        return new ParseTable();
    }
}