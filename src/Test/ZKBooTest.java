package Test;

import BooleanCircuit.Circuit;
import ZKBoo.Prover;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ZKBooTest
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
    public void testRandomnessGivesAdditiveShares() {
        Circuit circuit = new Circuit("src/BooleanCircuit/input/testXOR.txt");
        circuit.parseCircuit();
        int[][] gates = circuit.getGates();
        Prover prover = new Prover(0, gates,3, 3);
        boolean[][] shares = prover.getShares(0);
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
}
