package Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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


    public static byte[] hash(byte[] commit) {
        // make a commitment to the views, outputs, seeds etc.
        // send it to the verifier
        // make a hashChallenge by hashing the commitment

        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");

            //Assert.assertTrue(byte0 == );
            return messageDigest.digest(commit);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }
}
