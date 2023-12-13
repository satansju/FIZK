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

import static Util.Converter.convertBooleanArrayToByteArray;

public class Circuit {
    String path;
    int numberOfGates;
    int numberOfWires;
    int numberOfInputs;
    int numberOfOutputs;
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

        gates = new int[numberOfGates][4];
        String line2 = myReader.nextLine();
        String[] numbers2 = line2.split(" ", 3);
        numberOfInputs = Integer.parseInt(numbers2[0]);
        numberOfOutputs = Integer.parseInt(numbers2[2]);
        numberOfAndGates = 0;

        int i = 0;
        while (myReader.hasNext()) {
            String line = myReader.nextLine();
            String[] splitLine = line.split(" ", 6);
            /*System.out.println(Arrays.toString(splitLine));*/

            int gateType = GateType.valueOf(splitLine[splitLine.length - 1]).ordinal();
            if(gateType == GateType.AND.ordinal()) {
                numberOfAndGates += 1;
            }
            String input1 = splitLine[2];
            gates[i][0] = Integer.parseInt(input1);
            gates[i][2] = gateType;
            gates[i][3] = Integer.parseInt(splitLine[splitLine.length - 2]); // output wire

            if (gateType != GateType.INV.ordinal()) {
                String input2 = splitLine[3];
                gates[i][1] = Integer.parseInt(input2);
            }
            i += 1;
        }
    }

    public int[][] getGates() {
        return gates;
    }

    public int getNumberOfAndGates() {
        return numberOfAndGates;
    }

    public int getNumberOfInputs() {
        return numberOfInputs;
    }

    public int getNumberOfOutputs() {
        return numberOfOutputs;
    }

    public void parse(String path, Integer input) {
        this.path = path;
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
        numberOfWires = Integer.parseInt(numbers[1]);

        String line2 = myReader.nextLine();
        String[] numbers2 = line2.split(" ", 3);
        numberOfInputs = Integer.parseInt(numbers2[0]);
        numberOfOutputs = Integer.parseInt(numbers2[2]);
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
        byte[] bytes = convertBooleanArrayToByteArray(arr);
        output = new BigInteger(bytes);
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

    /**
     * For debugging purposes
     * @param hashMap
     * @param <T>
     * @param <K>
     */
    private <T, K> void printHashMap(HashMap<T, K> hashMap) {
        for (T k : hashMap.keySet()) {
            String key = k.toString();
            String value = hashMap.get(k).toString();
        }
    }


    /**
     * Evaluate a gate
     * @param op
     * @param inputs
     * @return
     */
    private boolean evalGate(String op, Integer[] inputs) {
        switch (op) {
            case "XOR":
                return wires.get(inputs[0]) ^ wires.get(inputs[1]);
            case "AND":
                return wires.get(inputs[0]) & wires.get(inputs[1]);
            case "INV":
                return !wires.get(inputs[0]);
            default:
                throw new Error("Gate " + op + " does not exist");
        }
    }
}
