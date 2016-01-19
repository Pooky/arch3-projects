/*
 * *****************************************************************************
 *
 * Computer Architecture Courseware 1.0
 *
 * Miroslav Skrbek (C) 2013,2014
 *
 * Free to use, copy and modify for non-commercial, educational and research
 * purposes only.
 *
 * NO WARRANTY OF ANY KIND.
 *
 *****************************************************************************
 */
package components.alus;

import generic.Alu;
import generic.PSW;

public class Alu16 extends Alu {
    
   public short exec(AluOp op, short src1, short src2, short dest, PSW psw) {
        return (short) Alu.exec(16, op, (long) src1, (long) src2, psw);
    }
   
}
