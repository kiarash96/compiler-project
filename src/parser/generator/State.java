package parser.generator;

import java.util.*;

/**
 * Created by kiarash on 6/14/17.
 */
public class State {
    Set<Item> items;

    public State(List<Production> grammar, Set<Item> kernel) {
        this.items = closure(grammar, kernel);
    }

    public State(List<Production> grammar, Item kernel) {
        this(grammar, new HashSet<Item>(Arrays.asList(kernel)));
    }

    private Set<Item> closure(List<Production> grammar, Set<Item> initialSet) {
        Set<Item> res = new HashSet<>(initialSet);
        while (true) {
            Queue<Item> toAdd = new LinkedList<>();
            for (Item item : res) {
                if (item.isReduce())
                    continue;
                String x = item.nextSymbol();
                for (Production p : grammar)
                    if (p.lhs.equals(x)) {
                        Item newItem = new Item(p, 0);
                        if (!res.contains(newItem))
                            toAdd.add(newItem);
                    }
            }

            if (toAdd.size() == 0)
                break;
            for (Item item : toAdd)
                res.add(item);
        }

        return res;
    }

    @Override
    public String toString() {
        String res = "";
        for (Item i : items)
            res += i.toString() + "\n";
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof State)
            return items.equals(((State) o).items);
        else
            return false;
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }
}
