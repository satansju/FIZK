package ZKBoo;

import Util.Tuple;

import javax.crypto.SecretKey;

public class Proof {
    public final boolean[][] outputShares;
    public final byte[] output;
    public final int party;
    public byte[] hashChallenge;
    public final byte[][] commitsArrays;
    public final Tuple<View> views;

    public final byte[] aArray;

    public Proof(
            byte[] output,
            int party,
            byte[] hashChallenge,
            byte[][] commitsArrays,
            boolean[][] outputShares,
            Tuple<View> views,
            byte[] aArray
    ) {
        this.output = output; // y
        this.party = party; // index of party
        this.hashChallenge = hashChallenge; // challenge e
        this.commitsArrays = commitsArrays; // c_e, c_e+1
        this.outputShares = outputShares; // y_1, y_2, y_3
        this.views = views; // w_e, w_e+1
        this.aArray = aArray;
    }
}
