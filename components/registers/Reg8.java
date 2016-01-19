
package components.registers;

import generic.Reg;

public class Reg8 extends Reg {
    private byte in;
    private byte out;
    
    public byte getQ() {
        return out;
    }
    
    public void setD(byte input) {
        in = input;
    }
    
    @Override
    public void clock() {
        out = in;
    }    
}
