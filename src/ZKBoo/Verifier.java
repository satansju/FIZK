package ZKBoo;

import BooleanCircuit.Shares;

import javax.crypto.SecretKey;
import java.util.HashMap;

import static Util.Util.*;
import static Util.Converter.convertBooleanArrayToByteArray;
import static ZKBoo.Prover.randomness;

// Verify the output of the prover is correct and reject otherwise
public class Verifier {
    View[] views;
    SecretKey[] seeds;
    int inputSize;
    int outputSize;
    int[][] gates;

    Shares sharesObj;
    private int input;
    private boolean[][] shares;
    private int party;
    private byte[] bArray;
    private byte[] zArray;

    public Verifier(int[][] gates) {
        this.gates = gates;

    }

    public void receiveProof(Proof proof) {
        this.views = proof.views;
        this.party = proof.party;
        this.seeds = proof.seedsForInputs;
        this.inputSize = proof.inputSize;
        this.outputSize = proof.outputSize;
        this.gates = proof.gates;
        this.input = proof.input;
        this.party = proof.party;
        this.zArray = proof.zArray;
        this.bArray = proof.bArray;

        sharesObj = new Shares(seeds);
        this.shares = sharesObj.getSharesForVerifier();

        byte[] xParty = retrieveXParty(party);
        byte[] xNextParty = retrieveXNextParty(nextParty(party));
        byte[] viewNextParty = convertBooleanArrayToByteArray(views[nextParty(party)].views);
        byte[] viewParty = calculateView(xParty, xNextParty, randomness[party], randomness[nextParty(party)]);

        if(verify()) { // TODO: implement verify()
            System.out.println("Proof is correct");
        } else {
            System.out.println("Proof is incorrect");
        }
    }

    private byte[] calculateView(byte[] xParty, byte[] xNextParty, boolean[] randomness, boolean[] randomness1) {
        byte[] view = new byte[xParty.length];
        return view;
    }

    private byte[] retrieveXNextParty(int i) {
        if(i == 0) {
            return convertBooleanArrayToByteArray(sharesObj.getShares(input)[0]);
        } else if(i == 1) {
            return convertBooleanArrayToByteArray(sharesObj.getShares(input)[1]);
        } else {
            System.arraycopy(zArray, zArray.length-outputSize, new byte[outputSize], 0, zArray.length);
            return convertBooleanArrayToByteArray(sharesObj.getShares(input)[2]);
        }
    }

    private byte[] retrieveXParty(int party) {
        if(party == 0) {
            return convertBooleanArrayToByteArray(sharesObj.getShares(input)[1]);
        } else if(party == 1) {
            System.arraycopy(zArray, zArray.length-outputSize, new byte[outputSize], 0, zArray.length);
            return convertBooleanArrayToByteArray(sharesObj.getShares(input)[2]);
        } else {
            System.arraycopy(bArray, bArray.length-outputSize, new byte[outputSize], 0, bArray.length);
            return convertBooleanArrayToByteArray(sharesObj.getShares(input)[0]);
        }
    }

    public boolean verify() {
        HashMap<Integer, Boolean> wires = new HashMap<>();
        for (int i = 0; i < inputSize; i++) {
            wires.put(i, true);
        }
        return true; // FIXME: implement
    }
}
