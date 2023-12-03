package Test;

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
}
