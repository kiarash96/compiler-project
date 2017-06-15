import parser.SLRparser;

import java.io.IOException;

/**
 * Created by kiarash on 5/30/17.
 */

public class Main {
    public static void main(String[] args) throws IOException {
        SLRparser parser = new SLRparser("simple");
        parser.parse();
    }
}
