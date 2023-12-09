package Test.FIZK;

import org.junit.Assert;
import org.junit.Test;

import static ZKBoo.Protocol.runProtocol;

/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class ProtocolTest {

    @Test
    public void testRunProtocolOnAndCircuit() throws Exception {
        String path = "src/BooleanCircuit/input/testAND.txt";
        for (int i = 0; i<4; i++) {
            boolean result = runProtocol(path, i);
            Assert.assertTrue(result);
        }
    }

    @Test
    public void testRunProtocolOn3AndSCircuit() throws Exception {
        String path = "src/BooleanCircuit/input/test3ANDS.txt";
        for (int i = 0; i<63; i++) {
            boolean result = runProtocol(path, i);
            Assert.assertTrue(result);
        }
    }

    @Test
    public void testRunProtocolOnChainedAndCircuit() throws Exception {
        String path = "src/BooleanCircuit/input/testChainedAND.txt";
        for (int i = 0; i<31; i++) {
            boolean result = runProtocol(path, i);
            Assert.assertTrue(result);
        }
    }

    @Test
    public void testRunProtocolOnXorCircuit() throws Exception {
        String path = "src/BooleanCircuit/input/testXOR.txt";
        for (int i = 0; i<4; i++) {
            boolean result = runProtocol(path, i);
            Assert.assertTrue(result);
        }
    }

    @Test
    public void testRunProtocolOnSHACircuit() throws Exception {
        String path = "src/BooleanCircuit/input/sha256.txt";
        for (int i = 0; i<8; i++) {
            boolean result = runProtocol(path, i);
            Assert.assertTrue(result);
        }
    }
}
