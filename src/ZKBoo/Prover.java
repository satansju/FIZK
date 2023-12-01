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

import static Util.Converter.convertByteArrayToBooleanArray;

public class Prover {
    View[] views;
    int[][] gates;
    int input;
    int numberOfAndGates;
    static int numberOfInputs;
    static byte[][] tapes;
    static boolean[][] randomness;


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

    private static char[] byteArrayToCharArray(byte[] byteArray, java.nio.charset.Charset charset) {
        // Convert byte array to String using the specified character set
        String str = new String(byteArray, charset);

        // Convert String to char array
        return str.toCharArray();
    }

    public static boolean[][] getShares(int input) {
        String strToEncrypt = "hejsa";
        String salt = "hejsa";
        boolean[][] shares = new boolean[3][512];


        for (int i = 0; i < 3; i++) {
            try {
                SecureRandom secureRandom = new SecureRandom();
                byte[] iv = new byte[16];
                secureRandom.nextBytes(iv);
                IvParameterSpec ivspec = new IvParameterSpec(iv);
                secureRandom.nextBytes(tapes[i]);
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                KeySpec spec = new PBEKeySpec(byteArrayToCharArray(tapes[i], StandardCharsets.UTF_8), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
                SecretKey tmp = factory.generateSecret(spec);
                SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

                byte[] cipherText = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
                byte[] encryptedData = new byte[iv.length + cipherText.length];
                System.arraycopy(iv, 0, encryptedData, 0, iv.length);
                System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);

                if (i != 2) {
                    shares[i] = convertByteArrayToBooleanArray(encryptedData);
                }
            } catch (Exception e) {
                // Handle the exception properly
                e.printStackTrace();
            }
        }
        System.out.println(Arrays.toString(shares));

        // take bits from res to get pseudorandom values

        // Make secret shares according to paper and return them
        //
        // Last tape is made by xor'ing each bit of the input with the corresponding bit
        // of each of the other two random tapes
        for (int i = 0; i < numberOfInputs; i++) {
            boolean ithInputBit = (input & 1) == 1;
            shares[2][i] = ithInputBit ^ shares[0][i] ^ shares[1][i];
            input = input >> 1;
        }

        return shares;
    }

    // Generate Randomness R_i for each party
    public void createRandomness() {
        // Create AES stream using AES in countermode with tapes[i] as key and 0 as IV
        // Encrypt 0 using AES stream to get R_i



        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < numberOfAndGates; j++) {
                randomness[i][j] = getRandomBit(false, convertByteArrayToBooleanArray(tapes[i]));
            }
        }

    }

    // Function R_i dependent on c and k_i - should be uniformly random // TODO: is it??
    public static boolean getRandomBit(boolean c, boolean[] tape) {
       boolean res = false;
        for (int i = 0; i < tape.length; i++) {
            if(i == 0) {
                res = c ^ tape[i];
            } else {
                res = res ^ (c ^ tape[i]);
            }
        }
        return res;
    }

    public static boolean getRandomBit(boolean c, int i, int j) {
        return c ^ randomness[i][j];
    }

    public void evaluateCircuit() {
        View partyA = views[0];
        View partyB = views[1];
        View partyC = views[2];

        for (int i = 0; i < gates.length + 1; i++) {
            int inputWire1 = gates[i][0];
            int inputWire2 = gates[i][1];
            int op = gates[i][2];

            switch (op) {
                case 0:
                    System.out.println("TODO: XOR");

                case 1:
                    System.out.println("TODO: AND");

                case 2:
                    System.out.println("TODO: INV");

                default:
                    throw new Error("Gate " + op + " does not exist");
            }

        }

        // should update views for the AND gates
    }

    public int getOutputs() {
        return 0;
    }

    public void hashChallenge() {
    }

    public void sendProofToVerifier() {
    }

    public void doMPCInTheHead() {
        /*
        We want to implement the ZKBoo like this:

            1) Sample random tapes k_1, k_2, k_3
            2) Compute (x_1, x_2, x_3) <- Share(x; k_1, k_2, k_3)
            3) Let w_1, w_2, w_3 be vectors of length N+1
            4) Compute views for the circuit for all, for all j = 1 to N, for all  i = 1 to 3: w_i[j]
            5) Compute y_i = output_i(w_i, k_i) for i = 1 to 3
            6) Generate output y = Rec(y_1, y_2, y_3)

        */

        // 1) and 2)
        boolean[][] shares = getShares(input);

        // 3)
        views[0] = new View(1, 1, 512); // TODO: ikke size 1 og seed 1 eller outputsize 512
        views[1] = new View(1, 1, 512);
        views[2] = new View(1, 1, 512);

        evaluateCircuit();
        int outputs = getOutputs();
        hashChallenge();
        sendProofToVerifier();
    }

    public static void main(String[] args) {
        boolean[] tape = {false, true, false, true};
        Circuit circuit = new Circuit("src/BooleanCircuit/input/sha256.txt");
        circuit.parseCircuit();
        int[][] gates = circuit.getGates();
        int input = 1;
        Prover prover = new Prover(input, gates, 256, circuit.getNumberOfAndGates());
        prover.createRandomness();


        boolean[][] arr = getShares(1);
    }
}
