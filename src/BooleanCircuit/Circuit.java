package BooleanCircuit;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import org.junit.Assert;

import Util.Converter.*;

import static Util.Converter.convertBooleanArrayToByteArray;

public class Circuit {
    String path;
    public Integer numberOfGates;
    Integer numberOfWires;
    Integer numberOfInputs;
    Integer numberOfOutputs;
    BigInteger output;
    HashMap<Integer, Boolean> wires = new HashMap<>();
    int[][] gates;
    int numberOfAndGates;

    public Circuit(String path) {
        this.path = path;
    }


    public void parseCircuit() {
        File file = new File(path);
        Scanner myReader;
        try {
            myReader = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        String line1 = myReader.nextLine();
        String[] numbers = line1.split(" ", 2);
        numberOfGates = Integer.parseInt(numbers[0]);

        gates = new int[numberOfGates][3];
        System.out.println(gates.length);
        System.out.println(gates[0].length);

        String line2 = myReader.nextLine();
        String[] numbers2 = line2.split(" ", 3);
        numberOfInputs = Integer.parseInt(numbers2[0]);
        numberOfOutputs = Integer.parseInt(numbers2[2]);
        numberOfAndGates = 0;

        int i = 0;
        while (myReader.hasNext()) {
            String line = myReader.nextLine();
            String[] splitLine = line.split(" ", 6);
            System.out.println(Arrays.toString(splitLine));

            int gateType = Gate.valueOf(splitLine[splitLine.length - 1]).ordinal();
            if(gateType == Gate.AND.ordinal()) {
                numberOfAndGates += 1;
            }
            String input1 = splitLine[3];
            gates[i][0] = Integer.parseInt(input1);
            gates[i][2] = gateType;

            if (gateType != Gate.INV.ordinal()) {
                String input2 = splitLine[4];
                gates[i][1] = Integer.parseInt(input2);
            }
            i += 1;
        }
        System.out.println("Number of And Gates is: " + numberOfAndGates);
    }

    public int[][] getGates() {
        return gates;
    }

    public int getNumberOfAndGates() {
        return numberOfAndGates;
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
        numberOfOutputs = Integer.parseInt(numbers2[2]);


        System.out.println(numberOfGates + " " + numberOfWires + " " + numberOfInputs + " " + numberOfOutputs);

        initializeValues(input);
// TODO: read in the circuit first and then evaluate
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



    private void readOutput() {
        // for each output wire in lowest layer, concat bits into final output bit string
        boolean[] arr = new boolean[numberOfOutputs];
        int firstIndex = numberOfWires - numberOfOutputs;
        // printHashMap(wires);

        for (int i = 0; i < numberOfOutputs; i++) {
            int idx = firstIndex + i;
            arr[i] = wires.get(idx);
        }

        /*byte[] outputByteArray = new byte[numberOfOutputs / 8];
        // convert boolean array to BigInteger
        byte bytes = Byte.parseByte(String.valueOf(arr));*/

        byte[] bytes = convertBooleanArrayToByteArray(arr);

        output = new BigInteger(bytes);
        System.out.println(output);
    }

    public BigInteger getOutput() {
        return output;
    }

    private void initializeValues(Integer input) {
        // Parse input to bit/bool array
        for (int i = 0; i < numberOfInputs; i++) {
            boolean bit = (input & 1) == 1;
            wires.put(i, bit);
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


    // TODO: something similar to evalGate that works for three parties
    // take all inputs from all three views and do the calculations here
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

        byte[] byte0 = BigInteger.ZERO.toByteArray();
        System.out.println(Arrays.toString(byte0));

        for (byte b : byte0) {
            Assert.assertEquals(0, b);
        }

        //Assert.assertTrue(byte0 == );
        BigInteger hashToBe = new BigInteger(digest.digest(byte0));

        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current absolute path is: " + s);
        String input = s + File.separator + "src" + File.separator + "BooleanCircuit" + File.separator + "input" + File.separator + "sha256.txt";
        System.out.println("Input: " + input);

        Circuit parser = new Circuit(input);

        parser.parseCircuit();
        // parser.parse(input, 0);

        // System.out.println("hashToBe");
        // System.out.println(hashToBe);
        // System.out.println("0 byte");
        // System.out.println(byte0.toString());


        // Assert.assertEquals(parser.getOutput(), hashToBe);

    }
}
