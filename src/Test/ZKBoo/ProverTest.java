package Test.ZKBoo;

import BooleanCircuit.Circuit;
import BooleanCircuit.Shares;
import ZKBoo.Prover;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static Util.Converter.convertBooleanArrayToByteArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ProverTest
{

    @Test
    public void testGetRandomBit() {

    }

    @Test
    public void testNextPartyDoesModCorrectly() {
        assertEquals(1, Prover.nextParty(0));
        assertEquals(2, Prover.nextParty(1));
        assertEquals(0, Prover.nextParty(2));
        assertEquals(1, Prover.nextParty(3));
    }

    @Test
    public void testRandomnessGivesAdditiveSharesZero() {
        /*Circuit circuit = new Circuit("src/BooleanCircuit/input/testXOR.txt");
        circuit.parseCircuit();
        int[][] gates = circuit.getGates();
        Prover prover = new Prover(0, gates,3, 3);*/
        // boolean[][] shares = prover.getShares(0);
        Shares sharesClass = new Shares();
        boolean[][] shares = sharesClass.getShares(0);
        boolean[] share1 = shares[0];
        boolean[] share2 = shares[1];
        boolean[] share3 = shares[2];
        boolean[] sum = new boolean[share1.length];

        for (int i = 0; i < share1.length; i++) {
            sum[i] = share1[i] ^ share2[i] ^ share3[i];
        }
        // Transform boolean sum array to integer
        int sumInt = 0;
        for (int i = 0; i < sum.length; i++) {
            if (sum[i]) {
                sumInt += Math.pow(2, i);
            }
        }
        assertEquals(0, sumInt);
    }

    @Test
    public void testRandomnessGivesAdditiveSharesOne() {
        Shares sharesClass = new Shares();
        boolean[][] shares = sharesClass.getShares(1);
        for (boolean[] share : shares) {
            assertTrue(share.length == 512);
        }
        boolean[] share1 = shares[0];
        boolean[] share2 = shares[1];
        boolean[] share3 = shares[2];
        boolean[] combinedShares = new boolean[share1.length];

        for (int i = 0; i < share1.length; i++) {
            combinedShares[i] = share1[i] ^ share2[i] ^ share3[i];
        }
        System.out.println("combined shares:" + Arrays.toString(combinedShares));
        byte[] test = {1};
        BigInteger testHejsa = new BigInteger(test);
        System.out.println("testHejsa: " + testHejsa);
        BigInteger combinedSharesBigInteger = new BigInteger(convertBooleanArrayToByteArray(combinedShares));
        System.out.println("Combined results" + combinedSharesBigInteger.toString());
        Assert.assertTrue(BigInteger.ONE.equals(combinedSharesBigInteger));
    }

    @Test
    public void testRandomnessGivesAdditiveShares666() {
        Shares sharesClass = new Shares();
        boolean[][] shares = sharesClass.getShares(666);
        for (boolean[] share : shares) {
            assertTrue(share.length == 512);
        }
        boolean[] share1 = shares[0];
        boolean[] share2 = shares[1];
        boolean[] share3 = shares[2];
        boolean[] combinedShares = new boolean[share1.length];

        for (int i = 0; i < share1.length; i++) {
            combinedShares[i] = share1[i] ^ share2[i] ^ share3[i];
        }
        System.out.println("combined shares:" + Arrays.toString(combinedShares));
        byte[] test = {1};
        BigInteger testHejsa = new BigInteger(test);
        System.out.println("testHejsa: " + testHejsa);
        BigInteger combinedSharesBigInteger = new BigInteger(convertBooleanArrayToByteArray(combinedShares));
        System.out.println("Combined results: " + combinedSharesBigInteger);
        Assert.assertTrue(BigInteger.valueOf(666L).equals(combinedSharesBigInteger));
    }
}
