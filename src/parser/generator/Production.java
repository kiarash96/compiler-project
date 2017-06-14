package parser.generator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kiarash on 6/13/17.
 */
public class Production {
    String lhs;
    String[] rhs;
    boolean isEpsilon;

    public Production(String lhs, List<String> rhs) {
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof Production) {
            Production other = (Production) o;
            return lhs.equals(other.lhs)
                    && Arrays.equals(rhs, other.rhs)
                    && isEpsilon == other.isEpsilon;
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        int res = lhs.hashCode();
        for (String rhs : rhs)
            res += rhs.hashCode();
        res += (isEpsilon ? 13 : 0);
        return res;
    }
}
