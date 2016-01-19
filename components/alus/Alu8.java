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

/**
 *
 * @author skrbek
 */
public class Alu8 extends Alu {

    public byte alu(AluOp op, byte src1, byte src2, PSW psw) {
        return (byte) Alu.exec(8, op, (long) src1, (long) src2, psw);
    }

}
