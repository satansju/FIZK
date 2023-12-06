package ZKBoo;

import javax.crypto.SecretKey;
import java.util.HashMap;

// Verify the output of the prover is correct and reject otherwise
public class Verifier {
    View[] views;
    SecretKey[] seeds;
    int inputSize;
    int outputSize;
    int[][] gates;

    public void receiveProof(Proof proof) {
        this.views = proof.views;
        this.seeds = proof.seedsForInputs;
        this.inputSize = proof.inputSize;
        this.outputSize = proof.outputSize;
        this.gates = proof.gates;

        if(verify()) { // TODO: implement verify()
            System.out.println("Proof is correct");
        } else {
            System.out.println("Proof is incorrect");
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
