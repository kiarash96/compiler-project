import parser.SLRParser;
import parser.generator.ParserGeneratorException;

import java.io.IOException;

/**
 * Created by kiarash on 5/30/17.
 */

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            SLRParser parser = new SLRParser("multiple-rets");
            parser.parse();
        } catch (ParserGeneratorException e) {
            System.out.println("Error in generating parse table: " + e.getMessage());
        }
    }
}
