package ZKBoo;

import javax.crypto.SecretKey;

public class Proof {
    public int input;
    public int party;
    public int[] randomness;
    byte[] hashChallenge;
    View[] views;
    SecretKey[] seedsForInputs;
    int inputSize;
    int outputSize;
    int[][] gates;
    byte[] bArray;
    byte[] zArray;

    public Proof(int input, int party, byte[] hashChallenge, View[] views, SecretKey[] seeds, int inputSize, int outputSize, int[][] gates, byte[] bArray, byte[] zArray){
        this.input = input;
        this.party = party;
        this.hashChallenge = hashChallenge;
        this.views = views;
        this.seedsForInputs = seeds;
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.gates = gates;
        this.bArray = bArray;
        this.zArray = zArray;
    }
}
