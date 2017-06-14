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
}
