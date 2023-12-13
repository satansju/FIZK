package ZKBoo;

import BooleanCircuit.Gate;
import BooleanCircuit.GateType;
import BooleanCircuit.Shares;
import Util.Tuple;

import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import static Util.Util.*;

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
    }

    public boolean[][] getOutputShares() {
        boolean[][] outputShares = new boolean[3][numberOfOutputs];
        // System.out.println(numberOfOutputs);
        for (int i = 0; i < 3; i++) {
            outputShares[i] = getOutput(wires.get(i), numberOfInputs, numberOfOutputs, gates.length);
        }
        return outputShares;
    }

    public byte[] getOutputAsByteArray() {
        return convertBooleanArrayToByteArray(output);
    }

    public void evaluateCircuit() {
        int andGateIdx = 0;
        for (int gateIdx = 0; gateIdx < gates.length; gateIdx++) {
            int inputWire1 = gates[gateIdx][0];
            int inputWire2 = gates[gateIdx][1];
            int outputWire = gates[gateIdx][3];
            int op = gates[gateIdx][2];

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
                /*if(gateIdx == 116629 - numberOfInputs) {
                    System.out.println(partyWires.get(116629));
                }*/
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
        // create random seeds, create randomness for AND gates and secret share
        // (1) Sample random seeds k_1, k_2, k_3
        Shares shareObj = new Shares();
        this.secretKeys = Shares.getSecretKeys();

        // (2) Compute (x_1, x_2, x_3) <- Share(x; k_1, k_2, k_3)
        this.shares = shareObj.getShares(input, numberOfInputs);
        this.randomness = shareObj.generateBitStreams(numberOfAndGates);

        // (3)
        this.views = new View[3];
        int viewSize = numberOfInputs + numberOfAndGates;

        // initialise views with the seeds that were used for the randomness for the AND gates - secretKey 2, 3 and 4 (0 and 1 were used for creating the input shares)
        this.views[0] = new View(viewSize, secretKeys[2], numberOfOutputs);
        this.views[1] = new View(viewSize, secretKeys[3], numberOfOutputs);
        this.views[2] = new View(viewSize, secretKeys[4], numberOfOutputs);


        // add shares to each party's view and the wires for computation:
        addShareToViewsAndWires(shares);

        // (4) Evaluate the boolean circuits and set the output and the output shares field variables
        evaluateCircuit();
        // (5)
        this.outputShares = getOutputShares();
        // (6)
        this.output = recoverOutput(outputShares);
    }

    private boolean[] recoverOutput(boolean[][] outputsShares) {
        boolean[] output = new boolean[outputsShares[0].length];
        for(int i = 0; i < outputsShares[0].length; i++) {
            output[i] = outputsShares[0][i] ^ outputsShares[1][i] ^ outputsShares[2][i];
        }
        return output;
    }

    Tuple<View> getZ(int challengeParty, View[] views) {
        if(challengeParty == 0) {
            return new Tuple<>(views[0], views[1]);
        } else if(challengeParty == 1) {
            return new Tuple<>(views[1], views[2]);
        } else if(challengeParty == 2) {
            return new Tuple<>(views[2], views[0]);
        } else {
            throw new Error("Party " + challengeParty + " does not exist");
        }
    }

    private void addShareToViewsAndWires(boolean[][] shares) {
        for (int i = 0; i < 3; i++) {
            // add all input bits for the share to the view
            boolean[] share = shares[i];
            this.views[i].addInputShare(share);
            this.wires.add(new HashMap<>());
            for (int j = 0; j < share.length; j++) {
                wires.get(i).put(j, share[j]);
            }
            // System.out.println(wires.get(i));
        }
    }

    public Proof sendProofToVerifier() {
        if(views == null) {
            throw new Error("Output has not been computed yet");
        }
        // obtain challenge (NON-INTERACTIVE) and do mod 3 to know which views to prepare
        // Prepare view e and e+1

        SecretKey[] seedsForProof = new SecretKey[]{secretKeys[2], secretKeys[3], secretKeys[4]};
        byte[][] commits = new byte[3][];

        for (int j = 0; j < 3; j++) {
            byte[] kj = seedsForProof[j].getEncoded();
            boolean[] vjBoolean = views[j].andGateEvaluations;
            byte[] vj = convertBooleanArrayToByteArray(vjBoolean);
            byte[] combined = new byte[kj.length + vj.length];
            ByteBuffer commitBuffer = ByteBuffer.wrap(combined);
            commitBuffer.put(kj);
            commitBuffer.put(vj);
            byte[] cj = commitBuffer.array();
            commits[j] = hash(cj);
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
        byte[][] bCommitsArray = commits;
        int challengeParty = Math.abs(convertByteArrayToInteger(challenge)) % 3;
        Tuple<View> zViewsForProof = getZ(challengeParty, views);
        byte[] y = convertBooleanArrayToByteArray(output);

        int countBytes = 0;
        countBytes += y.length;
        countBytes += 4;
        countBytes += challenge.length;
        countBytes += bCommitsArray[0].length;
        countBytes += bCommitsArray[1].length;
        countBytes += bCommitsArray[2].length;
        countBytes += outputShares[0].length;
        countBytes += outputShares[1].length;
        countBytes += outputShares[2].length;
        countBytes += zViewsForProof.a.andGateEvaluations.length;
        countBytes += zViewsForProof.b.andGateEvaluations.length;
        countBytes += aArray.length;
        countBytes += 2*3*4;
        countBytes += 2 * 32;
        // System.out.println("proof size is: "+ countBytes);


        return new Proof(
            y,
            challengeParty,
            challenge,
            bCommitsArray,
            outputShares,
            zViewsForProof,
            aArray
        );
    }
}
