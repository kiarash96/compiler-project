package lexical;

import java.util.ArrayList;

/**
 * Created by kiarash on 6/7/17.
 */
public class SymbolTable {
    public class Row {
        IDToken token;
        String type; // TODO: String?
        int scope;

        public Row(IDToken token) {
            this.token = token;
        }
    }

    ArrayList<Row> rows;

    public SymbolTable() {
        rows = new ArrayList<>();
    }

    public void insertNewRow(IDToken token) {
        rows.add(new Row(token));
    }

}
