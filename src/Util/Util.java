package Util;

import java.util.HashMap;

/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class Util {
    public static int nextParty(int i) {
        return (i + 1) % 3;
    }

    public static boolean[] getOutput(HashMap<Integer, Boolean> wires, int inputSize, int outputSize, int numberOfGates) {
        boolean[] output = new boolean[outputSize];
        for(int j = 0; j < outputSize; j++) {
            output[j] = wires.get(inputSize + numberOfGates - 1 - outputSize + j);
        }
        return output;
    }
}
