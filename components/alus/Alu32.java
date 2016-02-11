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
import generic.Alu.AluOp;
import generic.PSW;

public class Alu32 extends Alu{
    
   public int exec(AluOp op, int src1, int src2, int dest, PSW psw) {
        return (int) Alu.exec(32, op, (long) src1, (long) src2, psw);
    }
}
