package BooleanCircuit;

public class Gate {
    public static boolean evalXOR(boolean inputVal1, boolean inputVal2) {
        return inputVal1 ^ inputVal2;
    }

    public static boolean evalAND(boolean inputA1, boolean inputB1, boolean inputA2, boolean inputB2, boolean R1, boolean R2) {
        return (inputA1 & inputB1) ^ (inputA2 & inputB1) ^ (inputA1 & inputB2) ^ R1 ^ R2; // TODO - det her skal gøres hvor man får info fra de andre
    }

    public static boolean evalINV(boolean input) {
        return !input;
    }
}
