package ZKBoo;

import BooleanCircuit.Gate;
import BooleanCircuit.GateType;
import BooleanCircuit.Shares;
import Util.Tuple;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import static Util.Converter.convertBooleanArrayToByteArray;
import static Util.Util.*;

// Verify the output of the prover is correct and reject otherwise
public class Verifier {
    private final int[][] gates;
    private final int numberOfInputs;
    private final int numberOfOutputs;
    private final int numberOfAndGates;

    private byte[] output;
    private int party;
    private byte[] hashChallenge;
    private byte[][] commitsArrays;
    private boolean[][] outputShares;
    private Tuple<View> views;
    private byte[] aArray;
    private HashMap<Integer, Boolean> wiresParty = new HashMap<>();
    private HashMap<Integer, Boolean> wiresNextParty = new HashMap<>();

    public Verifier(int[][] gates, int numberOfInputs, int numberOfOutputs, int numberOfAndGates) {
        this.gates = gates;
        this.numberOfInputs = numberOfInputs;
        this.numberOfOutputs = numberOfOutputs;
        this.numberOfAndGates = numberOfAndGates;
    }

    /**
     * Receives a proof.
     *
     * @param proof the prof received
     */
    public void receiveProof(Proof proof) {
        this.output = proof.output;
        this.party = proof.party; // index of challenge party
        this.hashChallenge = proof.hashChallenge; // challenge e
        this.commitsArrays = proof.commitsArrays; // c_e, c_e+1
        this.outputShares = proof.outputShares; // b_e
        this.views = proof.views; // w_e, w_e+1
        this.aArray = proof.aArray;
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

        for (int i = 0; i < numberOfInputs; i++) {
            wiresParty.put(i, viewParty.andGateEvaluations[i]);
            wiresNextParty.put(i, viewNextParty.andGateEvaluations[i]);
        }

        int andGateIdx = 0;
        int viewIdx = numberOfInputs;
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
                    boolean r1 = randomBitStreamParty[andGateIdx];
                    boolean r2 = randomBitStreamNextParty[andGateIdx];
                    outputParty = Gate.evalAND(inputParty1, inputParty2, inputNextParty1, inputNextParty2, r1, r2);
                    outputNextParty = viewNextParty.andGateEvaluations[viewIdx];
                    if(outputParty != viewParty.andGateEvaluations[viewIdx]) {
                        return false;
                    }
                    andGateIdx++;
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

    /**
     * Verifies the proof received. For the verifier to accept the following must hold:
     * (1) If Rec(y_1, y_2, y_3) != y, reject
     * (2) If y_i != Output_i(w_i) for i in {e, e+1}, reject
     * (3) If w_received_e[j] != w_generated_e[j] for j in {1, ..., n}, reject
     *
     * @return true if the proof is correct
     */
    public boolean verify() {
        // Check that the reconstruction of the output y is correct
        // (1) If Rec(y_1, y_2, y_3) != y, reject
        byte[] yRecovered = recover(outputShares);
        byte[] y = output;

        if(!Arrays.equals(yRecovered, y)) {
            return false;
        }

        // Check for inconsistency of views
        // (2) If w_received_e[j] != w_generated_e[j] for j in {1, ..., n}, reject
        if (!checkView()) {
            return false;
        }

        // (3) If y_i != Output_i(w_i) for i in {e, e+1}, reject
        boolean[] yParty = outputShares[party];
        boolean[] yNextParty = outputShares[nextParty(party)];
        boolean[] viewParty = getOutput(wiresParty, numberOfInputs, numberOfOutputs, gates.length);
        boolean[] viewNextParty = getOutput(wiresNextParty, numberOfInputs, numberOfOutputs, gates.length);

        if(!Arrays.equals(yParty, viewParty) || !Arrays.equals(yNextParty, viewNextParty)) {
            return false;
        }

        // (4) Check that the commitments are correct
        if(!checkCommits()) {
            return false;
        }

        // (5) Check that the hashed challenge is correct (from aArray)
        return checkChallenge();
    }

    private boolean checkChallenge() {
        byte[] hashedAArray = hash(aArray);

        return Arrays.equals(hashedAArray, hashChallenge);
    }

    private boolean checkCommits() {
        byte[] commitParty = commitsArrays[party];
        byte[] commitNextParty = commitsArrays[nextParty(party)];
        View viewParty = views.a;
        View viewNextParty = views.b;

        return checkCommitment(commitParty, viewParty) && checkCommitment(commitNextParty, viewNextParty);
    }

    private boolean checkCommitment(byte[] commitParty, View view) {
        byte[] kParty = view.seed.getEncoded();
        byte[] vParty = convertBooleanArrayToByteArray(view.andGateEvaluations);
        byte[] combined = new byte[kParty.length + vParty.length];
        ByteBuffer commitBuffer = ByteBuffer.wrap(combined);
        commitBuffer.put(kParty);
        commitBuffer.put(vParty);
        byte[] cj = commitBuffer.array();
        byte[] generatedCommit = hash(cj);

        return Arrays.equals(generatedCommit, commitParty);
    }
}
