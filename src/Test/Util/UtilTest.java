package Test.Util;

import org.junit.Test;


import Util.Util;
import static org.junit.Assert.assertEquals;


public class UtilTest {

    @Test
    public void testNextPartyDoesModCorrectly() {
        assertEquals(1, Util.nextParty(0));
        assertEquals(2, Util.nextParty(1));
        assertEquals(0, Util.nextParty(2));
        assertEquals(1, Util.nextParty(3));
    }
}
