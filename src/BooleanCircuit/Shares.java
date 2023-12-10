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
    static SecretKey[] secretKeys;

    public Shares() {
        SecretKey secretKeyForShares1 = generateSecretKey();
        SecretKey secretKeyForShares2 = generateSecretKey();

        SecretKey secretKeyForAnd1 = generateSecretKey();
        SecretKey secretKeyForAnd2 = generateSecretKey();
        SecretKey secretKeyForAnd3 = generateSecretKey();

        this.secretKeys = new SecretKey[] {secretKeyForShares1, secretKeyForShares2, secretKeyForAnd1, secretKeyForAnd2, secretKeyForAnd3};
    }

    public Shares(SecretKey[] secretKeys) {
        if (secretKeys.length == 3) {
            this.secretKeys = new SecretKey[]{generateSecretKey(), generateSecretKey(), secretKeys[0], secretKeys[1], secretKeys[2]};
        }
    }

    // Generate a bit stream of random bits using AES encryption in counter mode of length int length
    public static boolean[] generateRandomBits(int length, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            int numberOfBytes = (int) Math.ceil(length / 8.0);
            byte[] bytes = new byte[length];
            byte[] counter = new byte[16];
            byte[] encryptedBytes = new byte[16];

            for (int i = 0; i < numberOfBytes; i += 16) {
                encryptedBytes = cipher.doFinal(counter);
                System.arraycopy(encryptedBytes, 0, bytes, i, Math.min(16, length - i));
                incrementCounter(counter);
            }

            boolean[] convertedBytes = convertByteArrayToBooleanArray(bytes);
            return Arrays.copyOfRange(convertedBytes, 0, length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean[][] generateBitStreams(int numberOfAndGates) {
        boolean[][] generatedBitStreams = new boolean[3][numberOfAndGates];
        try {
            generatedBitStreams[0] = generateRandomBits(numberOfAndGates, secretKeys[2]);
            generatedBitStreams[1] = generateRandomBits(numberOfAndGates, secretKeys[3]);
            generatedBitStreams[2] = generateRandomBits(numberOfAndGates, secretKeys[4]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedBitStreams;
    }

    private static void incrementCounter(byte[] counter) {
        for (int i = counter.length - 1; i >= 0; i--) {
            if (counter[i] == Byte.MAX_VALUE) {
                counter[i] = Byte.MIN_VALUE;
            } else {
                counter[i]++;
                break;
            }
        }
    }

    public static SecretKey[] getSecretKeys() {
        return secretKeys;
    }

    public static boolean[][] getShares(int x, int numberOfInputs) {
        try {
            // Generate a random secret key for AES with 256-bit key size


            // Generate random tapes k1, k2, k3
            boolean[] k1 = generateRandomBytes(secretKeys[0]);
            boolean[] k2 = generateRandomBytes(secretKeys[1]);

            // Calculate additive shares x1, x2, x3
            boolean[][] shares = calculateShares(x, numberOfInputs, k1, k2);
            return shares;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Generate a random secret key for AES with 256-bit key size
    public static SecretKey generateSecretKey() {
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
    private static boolean[][] calculateShares(int x, int numberOfInputs, boolean[] k1, boolean[] k2) throws Exception {
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
