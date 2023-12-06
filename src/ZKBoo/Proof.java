package ZKBoo;

import javax.crypto.SecretKey;

public class Proof {
    byte[] hashChallenge;
    View[] views;
    SecretKey[] seedsForInputs;
    int inputSize;
    int outputSize;
    int[][] gates;

    public Proof(byte[] hashChallenge, View[] views, SecretKey[] seeds, int inputSize, int outputSize, int[][] gates){
        this.views = views;
        this.seedsForInputs = seeds;
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.gates = gates;
    }
}
