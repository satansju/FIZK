package ZKBoo;

import BooleanCircuit.Circuit;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class Prover {
    View[] views;
    int[][] gates;
    int input;
    int numberOfInputs;

    public Prover(int input, int[][] gates, int numberOfInputs, int numberOfAndGates) {
        this.input = input;
        this.gates = gates;
        this.numberOfInputs = numberOfInputs;
        this.numberOfAndGates = numberOfAndGates;
        this.randomness = new boolean[3][22573];
        this.tapes = new byte[3][32];
    }

    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 65536;

    public boolean[][] getShares(int input) {
        String strToEncrypt = "hejsa";
        String secretKey = "hejsa";
        String salt = "hejsa";
        String res;
        String[] tapes = new String[3];
        for(int i = 0; i < 3; i++) {
            try {
                SecureRandom secureRandom = new SecureRandom();
                byte[] iv = new byte[16];
                secureRandom.nextBytes(iv);
                IvParameterSpec ivspec = new IvParameterSpec(iv);

                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
                SecretKey tmp = factory.generateSecret(spec);
                SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

                byte[] cipherText = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
                byte[] encryptedData = new byte[iv.length + cipherText.length];
                System.arraycopy(iv, 0, encryptedData, 0, iv.length);
                System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);

                tapes[i] = Base64.getEncoder().encodeToString(encryptedData);
            } catch (Exception e) {
                // Handle the exception properly
                e.printStackTrace();
            }
        }

        // take bits from res to get pseudorandom values

        // Make secret shares according to paper and return them
        for (int i = 0; i < numberOfInputs; i++) {
            boolean ithInputBit = (input & 1) == 1;
            boolean secretSharedBitch = false; // FIXME
            input = input >> 1;
        }

        return new boolean[1][1];
    }

    public void evaluateCircuit() {
        View partyA = views[0];
        View partyB = views[1];
        View partyC = views[2];

        for (int i = 0; i < gates.length + 1; i++) {
            int inputWire1 = gates[i][0];
            int inputWire2 = gates[i][1];
            int op = gates[i][2];
        }

        // should update views ?
    }

    public int getOutputs() {
        return 0;
    }

    public void hashChallenge() {}
    public void sendProofToVerifier() {}

    public void doMPCInTheHead() {
        boolean[][] shares = getShares(input);
        views[0] = new View(1, 1, 512);
        views[1] = new View(1, 1, 512);
        views[2] = new View(1, 1, 512);

        evaluateCircuit();
        int outputs = getOutputs();
        hashChallenge();
        sendProofToVerifier();
    }
}
