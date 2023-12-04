package BooleanCircuit;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;

import static Util.Converter.convertBooleanArrayToByteArray;
import static Util.Converter.convertByteArrayToBooleanArray;


/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class Shares {

    public static void main(String[] args) {
        try {
            // Generate a random secret key for AES with 256-bit key size
            SecretKey secretKey1 = generateSecretKey();
            SecretKey secretKey2 = generateSecretKey();
            SecretKey secretKey3 = generateSecretKey();

            // Input value x
            int x = 1; // Replace with your actual input

            // Generate random tapes k1, k2, k3
            boolean[] k1 = generateRandomBytes(secretKey1);
            boolean[] k2 = generateRandomBytes(secretKey2);
            boolean[] k3 = generateRandomBytes(secretKey3);

            // Calculate additive shares x1, x2, x3
            boolean[][] shares = calculateShares(x, k1, k2, k3);
            boolean[] x1 = shares[0];
            boolean[] x2 = shares[1];
            boolean[] x3 = shares[2];
            assert(x3.length == 512);

            // Convert to BigInteger for printing
            byte[] x1Bytes = convertBooleanArrayToByteArray(x1);
            byte[] x2Bytes = convertBooleanArrayToByteArray(x2);
            byte[] x3Bytes = convertBooleanArrayToByteArray(x3);


            BigInteger x1Int = new BigInteger(x1Bytes);
            BigInteger x2Int = new BigInteger(x2Bytes);
            BigInteger x3Int = new BigInteger(x3Bytes);

            boolean[] xCombined = new boolean[x1.length];

            for (int i = 0; i < x1.length; i++) {
                xCombined[i] = x1[i] ^ x2[i] ^ x3[i];
            }

            BigInteger xCombinedInt = new BigInteger(convertBooleanArrayToByteArray(xCombined));

            // Verify that x1 xor x2 xor x3 = x and x3 = x xor x1 xor x2
            System.out.println("x1: " + x1Int);
            System.out.println("x2: " + x2Int);
            System.out.println("x3: " + x3Int);
            System.out.println("x1 xor x2 xor x3: " + xCombinedInt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Generate a random secret key for AES with 256-bit key size
    private static SecretKey generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();
        keyGenerator.init(256, secureRandom);
        return keyGenerator.generateKey();
    }

    // Generate random booleans for the tapes
    private static boolean[] generateRandomBytes(SecretKey secretKey) throws Exception {
        byte[] bytes = new byte[51]; // 256-bit key size
        new SecureRandom().nextBytes(bytes);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(bytes);
        System.out.println("length of block of encryptedBytes: " + encryptedBytes.length);

        boolean[] convertedBytes = convertByteArrayToBooleanArray(encryptedBytes);
        System.out.println("length of AES block: " + convertedBytes.length);

        return convertedBytes;
    }

    // Calculate additive share using AES encryption
    private static boolean[][] calculateShares(int x, boolean[] k1, boolean[] k2, boolean[] k3) throws Exception {
        int numberOfInputs = 512; // TODO: change to 512
        boolean[][] shares = new boolean[3][numberOfInputs];
        boolean[] inputBits = intToBooleanArray(x, numberOfInputs);
        System.out.println(Arrays.toString(inputBits));

        for (int i = 0; i < numberOfInputs; i++) {
            shares[0][i] = k1[i];
            shares[1][i] = k2[i];
            shares[2][i] = inputBits[i] ^ k1[i] ^ k2[i];
            assert(inputBits[i] == shares[2][i] ^ shares[0][i] ^ shares[1][i]);
        }
        return shares;
    }

    // Convert int to byte array
    private static boolean[] intToBooleanArray(int x, int arrayLength) {
        boolean[] booleanArray = new boolean[arrayLength];
        for (int i = arrayLength-1; i >= 0; i--) {
            booleanArray[i] = (x & (1 << i)) != 0;
        }
        return booleanArray;
    }
}
