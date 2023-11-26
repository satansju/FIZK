import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import org.junit.Assert;


public class Parser {
    String path;
    Integer numberOfGates;
    Integer numberOfWires;
    Integer numberOfInputs;
    Integer numberOfOutputs;
    BigInteger output;

    HashMap<Integer, Boolean> wires = new HashMap<>();

    public Parser() {
    }

    public void parse(String path, Integer input) {
        this.path = path;

        //FileReader fileReader = new FileReader(input);
        File file = new File(path);
        Scanner myReader;
        // FileReader fileReader;
        try {
            myReader = new Scanner(file);
            // fileReader = new FileReader(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        String line1 = myReader.nextLine();
        String[] numbers = line1.split(" ", 2);
        numberOfGates = Integer.parseInt(numbers[0]);
        numberOfWires = Integer.parseInt(numbers[1]);

        String line2 = myReader.nextLine();
        String[] numbers2 = line2.split(" ", 3);
        numberOfInputs = Integer.parseInt(numbers2[0]);
        numberOfOutputs = Integer.parseInt(numbers2[2]);

        System.out.println(numberOfGates + " " + numberOfWires + " " + numberOfInputs + " " + numberOfOutputs);

        initializeValues(input);

        while (myReader.hasNext()) {
            String line = myReader.nextLine();
            String[] splitLine = line.split(" ", 6);
            int numberOfInputWires = Integer.parseInt(splitLine[0]);
            // In the SHA-256 circuit, there is 1 output wire for each gate, and this is placed as the second last element in a gate line
            int outputWireIndex = splitLine.length - 2;
            Integer outputWire = Integer.parseInt(splitLine[outputWireIndex]);
            String op = splitLine[splitLine.length - 1];
            Integer[] inputWires = new Integer[numberOfInputWires];

            for (int i = 0; i < numberOfInputWires; i++) {
                inputWires[i] = Integer.parseInt(splitLine[i + 2]);
            }

            boolean result = evalGate(op, inputWires);
            /*System.out.println(result);*/
            wires.put(outputWire, result);
        }

        readOutput();
    }

    byte[] convertBooleanArrayToByteArray(boolean[] arr) {
        if(arr.length < 8) {
            Integer number = 0;
            for (int i = 0; i<arr.length; i++) {
                if(arr[i]) {
                    number += (int) Math.pow(2,i);
                }
            }
            return new byte[]{number.byteValue()};
        }

        int n = arr.length/8;
        byte[] bytes = new byte[n];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i]) {
                bytes[i / 8] |= (byte) (1 << (7 - (i % 8)));
            }
            /*bytes[i] = Byte.parseByte(Arrays.asList(Arrays.copyOfRange(arr, i * 8, (i + 1) * 8)).stream().map(e -> Integer.parseInt(String.valueOf(e))).toString());*/
        }
        return bytes;
    }

    private void readOutput() {
        // for each output wire in lowest layer, concat bits into final output bit string
        boolean[] arr = new boolean[numberOfOutputs];
        int firstIndex = numberOfWires - numberOfOutputs;
        printHashMap(wires);

        for (int i = 0; i < numberOfOutputs; i++) {
            int idx = firstIndex + i;
            arr[i] = wires.get(idx);
        }

        /*byte[] outputByteArray = new byte[numberOfOutputs / 8];
        // convert boolean array to BigInteger
        byte bytes = Byte.parseByte(String.valueOf(arr));*/

        byte[] bytes =  convertBooleanArrayToByteArray(arr);

        output = new BigInteger(bytes);
        System.out.println(output);
    }

    public BigInteger getOutput() {
        return output;
    }

    private void initializeValues(Integer input) {
        // Parse input to bit/bool array
        for (int i = 0; i < numberOfInputs; i++) {
            wires.put(i, (input & 1) == 1);
            input = input >> 1;
        }

        // put actual inputs onto first 512 wires
        // numberOfInputs
        // printHashMap(wires);
    }

    private <T, K> void printHashMap(HashMap<T, K> hashMap) {
        for (T k : hashMap.keySet()) {
            String key = k.toString();
            String value = hashMap.get(k).toString();
            System.out.println(key + ": " + value);
        }
    }

    private boolean evalGate(String op, Integer[] inputs) {  // TODO: skal det kun være party 1 der f.eks. laver XOR? jeg tror det ikke, men det er værd at overveje
        switch (op) {
            case "XOR":
                return wires.get(inputs[0]) ^ wires.get(inputs[1]);
            case "AND":
                return wires.get(inputs[0]) & wires.get(inputs[1]);
            case "INV":
                return !wires.get(inputs[0]);
            default:
                System.out.println("Wrong input: " + op);
                throw new Error("Gate " + op + " does not exist");
        }
    }

    public static void main(String[] args) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hfdvn = "0".getBytes(StandardCharsets.UTF_8);
        BigInteger hashToBe = new BigInteger(digest.digest(hfdvn));

        Parser parser = new Parser();
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current absolute path is: " + s);
        String input = s + File.separator + "BooleanCircuitParser" + File.separator + "src" + File.separator + "input" + File.separator + "sha256.txt";
        System.out.println("Input: " + input);
        parser.parse(input, 0);

        System.out.println("hashToBe");
        System.out.println(hashToBe);
        System.out.println("hfdvn");
        System.out.println(hfdvn.toString());


        Assert.assertEquals(parser.getOutput(), hashToBe);

    }
}
