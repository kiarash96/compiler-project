package parser.generator;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by kiarash on 6/13/17.
 */
public class Production {
    String lhs;
    String[] rhs;
    boolean isEpsilon;

    public Production(String lhs, ArrayList<String> rhs) {
        this.lhs = new String(lhs);
        if (rhs.size() == 0 || (rhs.size() == 1 && rhs.get(0).equals("\""))) {
            this.rhs = new String[0];
            isEpsilon = true;
        } else {
            this.rhs = new String[rhs.size()];
            rhs.toArray(this.rhs);
            isEpsilon = false;
        }
    }

    @Override
    public String toString() {
        String res = lhs + " ->";
        for (String x : rhs)
            res += " " + x;
        return res;
    }
}
