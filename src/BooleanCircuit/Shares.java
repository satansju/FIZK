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
import static Util.Converter.intToBooleanArray;


/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class Shares {

    // Secret keys
    static SecretKey[] secretKeys = new SecretKey[3];

    public Shares() {
        SecretKey secretKey1 = generateSecretKey();
        SecretKey secretKey2 = generateSecretKey();
        SecretKey secretKey3 = generateSecretKey();

        secretKeys[0] = secretKey1;
        secretKeys[1] = secretKey2;
        secretKeys[2] = secretKey3;
    }

    public static SecretKey[] getSecretKeys() {
        return secretKeys;
    }

    public static boolean[][] getShares(int x) {
        try {
            // Generate a random secret key for AES with 256-bit key size


            // Generate random tapes k1, k2, k3
            boolean[] k1 = generateRandomBytes(secretKeys[0]);
            boolean[] k2 = generateRandomBytes(secretKeys[1]);
            boolean[] k3 = generateRandomBytes(secretKeys[2]);

            // Calculate additive shares x1, x2, x3
            boolean[][] shares = calculateShares(x, k1, k2, k3);
            return shares;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Generate a random secret key for AES with 256-bit key size
    private static SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = new SecureRandom();
            keyGenerator.init(256, secureRandom);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
        int numberOfInputs = 512;
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
}
