package parser.generator;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by kiarash on 6/13/17.
 */
public class Production {
    String lhs;
    String[] rhs;

    public Production(String lhs, ArrayList<String> rhs) {
        this.lhs = new String(lhs);
        this.rhs = new String[rhs.size()];
        rhs.toArray(this.rhs);
    }

    @Override
    public String toString() {
        String res = lhs + " ->";
        for (String x : rhs)
            res += " " + x;
        return res;
    }
}
