package parser.generator;

import java.util.Arrays;

/**
 * Created by kiarash on 6/14/17.
 */
public class ParseTable {
    public String[] gotoHead;
    public int[][] gotoTable;

    public String[][] grammar;

    public Object[] actionTableHead;
    public String[][] actionTable;

    public Object[][] follows;

    @Override
    public String toString() {
        String res = "";

        res += "goto table:\n" + Arrays.toString(gotoHead) + "\n";
        for (int i = 0; i < gotoTable.length; i ++)
            res += "s" + i + ": " + Arrays.toString(gotoTable[i]) + "\n";

        res += "\naction table:\n" + Arrays.toString(actionTableHead) + "\n";
        for (int i = 0; i < actionTable.length; i ++)
            res += "s" + i + ": " + Arrays.toString(actionTable[i]) + "\n";

        return res;
    }
}
