package parser.generator;

import parser.ParseTable;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by kiarash on 6/13/17.
 */
public class SLRTableGenerator {
    ArrayList<Production> grammar;

    public SLRTableGenerator() {
        grammar = new ArrayList<>();
    }

    public ParseTable generate(Scanner scanner) {
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

        return new ParseTable();
    }
}
