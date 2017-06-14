package parser.generator;

/**
 * Created by kiarash on 6/13/17.
 */
public class Item {
    Production p;
    int dotPos;

    public Item(Production p, int dotPos) {
        this.p = p;
        this.dotPos = dotPos;
    }

    public boolean isReduce() {
        return p.isEpsilon || dotPos == p.rhs.length;
    }

    public String nextSymbol() {
        if (isReduce())
            return null;
        else
            return p.rhs[dotPos];
    }

    public Item next() {
        if (isReduce())
            return null;
        else
            return new Item(p, dotPos + 1);
    }

    @Override
    public String toString() {
        String res = p.lhs + " ->";
        for (int i = 0; i < p.rhs.length; i ++)
            res += (dotPos == i ? " . " : " ") + p.rhs[i];
        if (dotPos == p.rhs.length)
            res += " .";
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Item) {
            Item other = (Item) o;
            return p.equals(other.p)
                    && dotPos == other.dotPos;
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        return p.hashCode() + dotPos;
    }
}
