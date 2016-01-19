
package components.registers;

import generic.Reg;

public class Reg16 extends Reg {
    private short in;
    private short out;
    
    public short getQ() {
        return out;
    }
    
    public void setD(short input) {
        in = input;
    }
    
    @Override
    public void clock() {
        out = in;
    }
}
