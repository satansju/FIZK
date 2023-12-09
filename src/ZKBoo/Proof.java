package ZKBoo;

import Util.Tuple;

import javax.crypto.SecretKey;

public class Proof {
    public final boolean[][] outputShares;
    public final byte[] output;
    public final int party;
    public final int numberOfAndGates;
    public byte[] hashChallenge;
    public SecretKey[] seedsForInputs;
    public int inputSize;
    public int outputSize;
    public int[][] gates;
    public final byte[][] commitsArrays;
    public final Tuple<View> views;

    public Proof(
            byte[] output,
            int party,
            byte[] hashChallenge,
            int inputSize,
            int outputSize,
            int numberOfAndGates,
            int[][] gates,
            byte[][] commitsArrays,
            boolean[][] outputShares,
            Tuple<View> views
    ){
        this.output = output; // y
        this.party = party; // index of party
        this.hashChallenge = hashChallenge; // challenge e
        this.inputSize = inputSize; // n
        this.outputSize = outputSize; // m
        this.numberOfAndGates = numberOfAndGates; // number of AND gates
        this.gates = gates; // gates from boolean circuit
        this.commitsArrays = commitsArrays; // c_e, c_e+1
        this.outputShares = outputShares; // b_e
        this.views = views; // w_e, w_e+1
    }
}
