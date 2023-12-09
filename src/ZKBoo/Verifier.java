package ZKBoo;

import BooleanCircuit.Gate;
import BooleanCircuit.GateType;
import BooleanCircuit.Shares;
import Util.Tuple;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.HashMap;

import static Util.Converter.convertBooleanArrayToByteArray;
import static Util.Util.getOutput;
import static Util.Util.nextParty;

// Verify the output of the prover is correct and reject otherwise
public class Verifier {
    Tuple<View> views;
    SecretKey[] seeds;
    int inputSize;
    int outputSize;
    int[][] gates;

    Shares sharesObj;
    private boolean[][] shares;
    private int party;
    private boolean[][] outputShares;
    private byte[][] commitsArrays;
    private byte[] hashChallenge;
    private byte[] output;

    HashMap<Integer, Boolean> wiresParty = new HashMap<>();
    HashMap<Integer, Boolean> wiresNextParty = new HashMap<>();
    private int numberOfAndGates;

    public Verifier(int[][] gates) {
        this.gates = gates;

    }

    public boolean receiveProof(Proof proof) {
        this.output = proof.output;
        this.party = proof.party; // index of party
        this.hashChallenge = proof.hashChallenge; // challenge e
        this.inputSize = proof.inputSize; // n
        this.outputSize = proof.outputSize; // m
        this.numberOfAndGates = proof.numberOfAndGates; // number of AND gates
        this.gates = proof.gates; // gates from boolean circuit
        this.commitsArrays = proof.commitsArrays; // c_e, c_e+1
        this.outputShares = proof.outputShares; // b_e
        this.views = proof.views; // w_e, w_e+1

        if(verify()) {
            System.out.println("Proof is correct");
            return true;
        } else {
            System.out.println("Proof is incorrect");
            return false;
        }
    }

    private byte[] recover(boolean[][] outputShares) {
        // Recover the output from the output shares
        boolean[] yRecovered = new boolean[outputShares[0].length];
        boolean[] yRecoveredPart1 = outputShares[0];
        boolean[] yRecoveredPart2 = outputShares[1];
        boolean[] yRecoveredPart3 = outputShares[2];
        for (int j = 0; j < yRecoveredPart1.length; j++) {
            yRecovered[j] = yRecoveredPart1[j] ^ yRecoveredPart2[j] ^ yRecoveredPart3[j];
        }
        return convertBooleanArrayToByteArray(yRecovered);
    }

    private boolean checkView() {
        // Do partial parse of the circuit given by gates, and generate the view for the party
        // Check that the view is consistent with the output shares
        View viewParty = views.a;
        View viewNextParty = views.b;

        boolean[] randomBitStreamParty = Shares.generateRandomBits(numberOfAndGates, viewParty.seed);
        boolean[] randomBitStreamNextParty = Shares.generateRandomBits(numberOfAndGates, viewNextParty.seed);

        wiresParty = new HashMap<>();
        wiresNextParty = new HashMap<>();

            for (int i = 0; i < inputSize; i++) {
                wiresParty.put(i, viewParty.views[i]);
                wiresNextParty.put(i, viewNextParty.views[i]);
            }

        int viewIdx = inputSize;
        for (int gateIdx = 0; gateIdx < gates.length; gateIdx++) {
            int[] gate = gates[gateIdx];
            int inputWireIdx1 = gate[0];
            int inputWireIdx2 = gate[1];
            int op = gate[2];
            int outputWireIdx = gate[3];

            boolean inputParty1 = wiresParty.get(inputWireIdx1);
            boolean inputParty2 = wiresParty.get(inputWireIdx2);
            boolean inputNextParty1 = wiresNextParty.get(inputWireIdx1);
            boolean inputNextParty2 = wiresNextParty.get(inputWireIdx2);

            boolean outputParty;
            boolean outputNextParty;
            switch (GateType.values()[op]) {
                case XOR:
                    outputParty = Gate.evalXOR(inputParty1, inputParty2);
                    outputNextParty = Gate.evalXOR(inputNextParty1, inputNextParty2);
                    break;
                case AND:
                    boolean r1 = randomBitStreamParty[gateIdx];
                    boolean r2 = randomBitStreamNextParty[gateIdx];
                    outputParty = Gate.evalAND(inputParty1, inputParty2, inputNextParty1, inputNextParty2, r1, r2);
                    outputNextParty = viewNextParty.views[viewIdx];

                    if(outputParty != viewParty.views[viewIdx]) {
                        return false;
                    }

                    viewIdx++;
                    break;
                case INV:
                    outputParty = Gate.evalINV(inputParty1);
                    outputNextParty = Gate.evalINV(inputNextParty1);
                    break;
                default:
                    throw new Error("Gate " + op + " does not exist");
            }
            wiresParty.put(outputWireIdx, outputParty);
            wiresNextParty.put(outputWireIdx, outputNextParty);
        }

        return true;
    }

    public boolean verify() {
        /*
        (1) If Rec(y_1, y_2, y_3) != y, reject
        (2) If y_i != Output_i(w_i) for i in {e, e+1}, reject
        (3) If w_received_e[j] != w_generated_e[j] for j in {1, ..., n}, reject
        (4) Output accept
         */

        // (1) If Rec(y_1, y_2, y_3) != y, reject
        byte[] yRecovered = recover(outputShares);
        byte[] y = output;

        if(!Arrays.equals(yRecovered, y)) {
            return false;
        }

        // (3) If w_received_e[j] != w_generated_e[j] for j in {1, ..., n}, reject
        if (!checkView()) {
            return false;
        }

        // (2) If y_i != Output_i(w_i) for i in {e, e+1}, reject
        boolean[] yParty = outputShares[party];
        boolean[] yNextParty = outputShares[nextParty(party)];
        boolean[] viewParty = getOutput(wiresParty, inputSize, outputSize, gates.length);
        boolean[] viewNextParty = getOutput(wiresNextParty, inputSize, outputSize, gates.length);

        if(!Arrays.equals(yParty, viewParty) || !Arrays.equals(yNextParty, viewNextParty)) {
            return false;
        }

        return true; // FIXME: implement
    }
}
