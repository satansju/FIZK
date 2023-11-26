import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

class Test {

    public static void main(String[]args){
        Parser parser = new Parser();
        /*String input = "input/sha256.txt";*/
        /*String input = "input/testAND.txt";*/
        Path currentRelativePath = Paths.get("");

        String s = currentRelativePath.toAbsolutePath().toString();

        String input = s + File.separator + "BooleanCircuitParser" + File.separator + "src" + File.separator + "input" + File.separator + "testXOR.txt";

        parser.parse(input, 1);
    }
}


