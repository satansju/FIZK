package ZKBoo;

import BooleanCircuit.Circuit;
import BooleanCircuit.Gate;
import BooleanCircuit.GateType;
import BooleanCircuit.Shares;
import Util.Converter;
import org.junit.Assert;

import javax.crypto.SecretKey;
import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static Util.Converter.*;

public class Prover {
    View[] views;
    int[][] gates;
    int input;
    int numberOfAndGates;
    static int numberOfInputs;
    static int numberOfOutputs;
    static boolean[][] shares;
    static boolean[][] randomness;

    List<HashMap<Integer, Boolean>> wires;
    SecretKey[] secretKeys;

    boolean[] output;

    boolean[][] outputShares;

    public Prover(int input, int[][] gates, int numberOfInputs, int numberOfOutputs, int numberOfAndGates) {
        this.input = input;
        this.gates = gates;
        this.numberOfInputs = numberOfInputs;
        this.numberOfOutputs = numberOfOutputs;
        this.numberOfAndGates = numberOfAndGates;
        this.wires = new ArrayList<>();

        // create random seeds, create randomness for AND gates and secret share ((1) and (2))
        Shares shareObj = new Shares();
        // (1) Sample random seeds k_1, k_2, k_3
        this.secretKeys = Shares.getSecretKeys();
        // (2) Compute (x_1, x_2, x_3) <- Share(x; k_1, k_2, k_3)
        this.shares = shareObj.getShares(input);
        this.randomness = shareObj.generateBitStreams(numberOfAndGates);
    }

    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 65536;

    private static char[] byteArrayToCharArray(byte[] byteArray, java.nio.charset.Charset charset) {
        // Convert byte array to String using the specified character set
        String str = new String(byteArray, charset);

        // Convert String to char array
        return str.toCharArray();
    }

    public void evaluateCircuit() {
        int andGateIdx = 0;
        for (int gateIdx = 0; gateIdx < gates.length; gateIdx++) {
            int inputWire1 = gates[gateIdx][0];
            int inputWire2 = gates[gateIdx][1];
            int op = gates[gateIdx][2];
            int outputWire = gates[gateIdx][3];

            for (int party = 0; party < 3; party++) {
                HashMap<Integer, Boolean> partyWires = wires.get(party);
                boolean output;
                boolean input1 = partyWires.get(inputWire1);
                boolean input2 = partyWires.get(inputWire2);

                switch (GateType.values()[op]) {
                    case XOR:
                        output = Gate.evalXOR(input1, input2);
                        break;
                    case AND:
                        HashMap<Integer, Boolean> wireNextParty = wires.get(nextParty(party));
                        boolean inputNextParty1 = wireNextParty.get(inputWire1);
                        boolean inputNextParty2 = wireNextParty.get(inputWire2);

                        // FIXME - learn how to get the randomness // TODO - det her skal gøres hvor man får info fra de andre parter
                        output = Gate.evalAND(input1, input2, inputNextParty1, inputNextParty2, randomness[party][andGateIdx], randomness[nextParty(party)][andGateIdx]);
                        break;
                    case INV:
                        output = Gate.evalINV(input1);
                        break;
                    default:
                        throw new Error("Gate " + op + " does not exist");
                }

                // put the output value on the output wire of the gate
                partyWires.put(outputWire, output); // TODO: test that this is actually placed in the correct position
                if(gateIdx == 116629 - numberOfInputs) {
                    System.out.println(partyWires.get(116629));
                }
                // put the output in the view if the gate was an AND gate
                if (GateType.values()[op] == GateType.AND) {
                    views[party].updateView(output);
                }

            }
            if(GateType.values()[op] == GateType.AND) {
                andGateIdx++;
            }


        }

    }

    public static int nextParty(int i) {
        return (i + 1) % 3;
    }

    public boolean[][] getOutputShares() {
        boolean[][] outputShares = new boolean[3][numberOfOutputs];
        System.out.println(numberOfOutputs);
        for (int i = 0; i < 3; i++) {
            /*boolean[] outputShare = new boolean[numberOfOutputs];*/

            for(int j = 0; j < numberOfOutputs; j++) {
                outputShares[i][j] = wires.get(i).get(gates.length - 1 - numberOfOutputs + j);
            }

            /*System.arraycopy( wires.get(i).values().toArray(), (wires.get(i).size() - 1 - 256), outputShare, 0, 256);*/
            /*outputShares[i] = outputShare;*/
            /*System.out.println("Output " + i + ": " + Arrays.toString(outputShare));*/
            System.out.println(Arrays.toString(outputShares[i]));
            System.out.println("party " + i + " outputshare: " + convertBooleanArrayToInteger(outputShares[i]));
        }
        return outputShares;
    }

    public void doMPCInTheHead() {
        /*
        We want to implement the ZKBoo like this:

            (1) Sample random tapes k_1, k_2, k_3
            (2) Compute (x_1, x_2, x_3) <- Share(x; k_1, k_2, k_3)
            (3) Let w_1, w_2, w_3 be vectors of length N+1
            (4) Compute views for the circuit for all, for all j = 1 to N, for all  i = 1 to 3: w_i[j]
            (5) Compute y_i = output_i(w_i, k_i) for i = 1 to 3
            (6) Generate output y = Rec(y_1, y_2, y_3)

        */
        // 1) and 2) are done in the constructor

        // 3)
        views = new View[3];
        int viewSize = numberOfInputs + numberOfAndGates;

        // initialise views with the seeds that were used for the randomness for the AND gates - secretKey 2, 3 and 4 (0 and 1 were used for creating the input shares)
        views[0] = new View(viewSize, secretKeys[2], 256);
        views[1] = new View(viewSize, secretKeys[3], 256);
        views[2] = new View(viewSize, secretKeys[4], 256);


        // add shares to each party's view and the wires for computation:
        addShareToViewsAndWires(shares);

        evaluateCircuit();
        System.out.println("Views: " + Arrays.toString(views));
        System.out.println("View lengths: " + views[0].views.length + ", " + views[1].views.length + ", " + views[2].views.length);
        this.outputShares = getOutputShares();
        this.output = recoverOutput(outputShares);
        System.out.println("OutputCombined: " + Arrays.toString(output));
        System.out.println("Output: " + convertBooleanArrayToInteger(output));

    }

    private boolean[] recoverOutput(boolean[][] outputsShares) {
        boolean[] output = new boolean[outputsShares[0].length];
        for(int i = 0; i < outputsShares[0].length; i++) {
            output[i] = outputsShares[0][i] ^ outputsShares[1][i] ^ outputsShares[2][i];
        }
        return output;
    }


    public byte[] hash(byte[] commit) {
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
        }

        return null; // TODO
    }

    public Proof sendProofToVerifier() {
        if(views == null) {
            throw new Error("Output has not been computed yet");
        }
        // obtain challenge (NON-INTERACTIVE) and do mod 3 to know which views to prepare
        // prepare view e and e+1

        View[] viewsForProof = new View[]{views[0], views[1]};
        SecretKey[] seedsForProof = new SecretKey[]{secretKeys[2], secretKeys[3], secretKeys[4]};
        byte[][] commits = new byte[3][];
        byte[][] decommits = new byte[3][];

        for (int j = 0; j < 3; j++) {
            byte[] kj = seedsForProof[j].getEncoded();
            byte[] xj = convertBooleanArrayToByteArray(shares[j]);
            boolean[] vjBoolean = views[j].views;
            byte[] vj = convertBooleanArrayToByteArray(vjBoolean);
            byte[] combined = new byte[kj.length + xj.length + vj.length];
            ByteBuffer commitBuffer = ByteBuffer.wrap(combined);
            commitBuffer.put(kj);
            commitBuffer.put(xj);
            commitBuffer.put(vj);
            byte[] cj = commitBuffer.array();
            commits[j] = hash(cj);

            byte[] decommitCombined = new byte[kj.length + xj.length];
            ByteBuffer decommitCombinedBuffer = ByteBuffer.wrap(decommitCombined);
            decommitCombinedBuffer.put(kj);
            decommitCombinedBuffer.put(xj);
            byte[] decommitCombinedArray = decommitCombinedBuffer.array();
            decommits[j] = decommitCombinedArray;
        }

        int outputSharesLength = outputShares[0].length + outputShares[1].length + outputShares[2].length;
        int commitsLength = commits[0].length + commits[1].length + commits[2].length;
        byte[] a = new byte[outputSharesLength + commitsLength];
        ByteBuffer aBuffer = ByteBuffer.wrap(a);
        aBuffer.put(convertBooleanArrayToByteArray(outputShares[0]));
        aBuffer.put(convertBooleanArrayToByteArray(outputShares[1]));
        aBuffer.put(convertBooleanArrayToByteArray(outputShares[2]));
        aBuffer.put(commits[0]);
        aBuffer.put(commits[1]);
        aBuffer.put(commits[2]);
        byte[] aArray = aBuffer.array();
        byte[] challenge = hash(aArray);

        int challengeParty = convertByteArrayToInteger(challenge) % 3;
        int previousParty = (challengeParty + 2) % 3;

        byte[] bi = new byte[outputShares[previousParty].length + commits[previousParty].length];
        ByteBuffer biBuffer = ByteBuffer.wrap(bi);
        biBuffer.put(convertBooleanArrayToByteArray(outputShares[previousParty]));
        biBuffer.put(commits[previousParty]);
        byte[] biArray = biBuffer.array();
        byte[] zArray = generateZ(challengeParty, viewsForProof, seedsForProof, this.shares);

        return new Proof(challenge, viewsForProof, seedsForProof, numberOfInputs, numberOfOutputs, gates, biArray, zArray);
    }

    byte[] generateZ(int challengeParty, View[] views, SecretKey[] secretKeys, boolean[][] shares) {
        byte[] z;
        ByteBuffer zBuffer;
        byte[] zArray;
        if(challengeParty == 0) {
            z = new byte[views[1].views.length + secretKeys[0].getEncoded().length + secretKeys[1].getEncoded().length];
            zBuffer = ByteBuffer.wrap(z);
            zBuffer.put(convertBooleanArrayToByteArray(views[1].views));
            zBuffer.put(secretKeys[0].getEncoded());
            zBuffer.put(secretKeys[1].getEncoded());
        } else if(challengeParty == 1) {
            z = new byte[views[2].views.length + secretKeys[1].getEncoded().length + secretKeys[2].getEncoded().length + shares[2].length];
            zBuffer = ByteBuffer.wrap(z);
            zBuffer.put(convertBooleanArrayToByteArray(views[2].views));
            zBuffer.put(secretKeys[1].getEncoded());
            zBuffer.put(secretKeys[2].getEncoded());
            zBuffer.put(convertBooleanArrayToByteArray(shares[2]));
        } else if(challengeParty == 2) {
            z = new byte[views[0].views.length + secretKeys[2].getEncoded().length + secretKeys[0].getEncoded().length + shares[2].length];
            zBuffer = ByteBuffer.wrap(z);
            zBuffer.put(convertBooleanArrayToByteArray(views[0].views));
            zBuffer.put(secretKeys[2].getEncoded());
            zBuffer.put(secretKeys[0].getEncoded());
            zBuffer.put(convertBooleanArrayToByteArray(shares[0]));
        } else {
            throw new Error("Party " + challengeParty + " does not exist");
        }
        zArray = zBuffer.array();
        return zArray;
    }

    private int convertByteArrayToInteger(byte[] challenge) {
        return new BigInteger(challenge).intValue();
    }

    private void addShareToViewsAndWires(boolean[][] shares) {
        for (int i = 0; i < 3; i++) {
            // add all input bits for the share to the view
            boolean[] share = shares[i];
            views[i].addInputShare(share);
            wires.add(new HashMap<>());
            wires.get(i).put(0, false);
            for (int j = 0; j < share.length; j++) {
                wires.get(i).put(j, share[j]);
            }
            System.out.println(wires.get(i));
        }
    }
}
