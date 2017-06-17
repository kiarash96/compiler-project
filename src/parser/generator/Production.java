package parser.generator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kiarash on 6/13/17.
 */
public class Production {
    String lhs;
    String[] rhs;

    public Production(String lhs, List<String> rhs) {
        this.lhs = new String(lhs);
        if (rhs.size() == 0 || (rhs.size() == 1 && rhs.get(0).equals("''")))
            this.rhs = new String[0];
        else {
            this.rhs = new String[rhs.size()];
            rhs.toArray(this.rhs);
        }
    }

    public boolean isEpsilon() {
        return rhs.length == 0;
    }

    @Override
    public String toString() {
        String res = lhs + " ->";
        for (String x : rhs)
            res += " " + x;
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Production) {
            Production other = (Production) o;
            return lhs.equals(other.lhs)
                    && Arrays.equals(rhs, other.rhs);
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        int res = lhs.hashCode();
        for (String rhs : rhs)
            res += rhs.hashCode();
        return res;
    }
}
