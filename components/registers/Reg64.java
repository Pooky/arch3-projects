package components.registers;

import generic.Reg;

/**
 *
 * @author skrbek
 */
public class Reg64 extends Reg {
    private long in;
    private long out;
    
    public long getQ() {
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
