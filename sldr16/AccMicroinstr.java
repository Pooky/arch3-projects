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
package sldr16;

import generic.Alu.AluOp;
import generic.Microinstruction;

public class AccMicroinstr extends Microinstruction {
    AluOp aluop;
    int src1s;
    int src2s;
    int k;    
    boolean moe;
    boolean mwr;
    boolean doe;
    boolean aoe;
    int asel;
    boolean aw;
    boolean pcw;
    boolean irw;
    boolean psww;
    int pswsel;
    
    public AccMicroinstr(
    Microinstruction.Condition cond,
    int targetState,        
    AluOp aluop,
    int src1s,
    int src2s,
    int k,    
    boolean moe,
    boolean mwr,
    boolean doe,
    boolean aoe,
    int asel,
    boolean aw,
    boolean pcw,
    boolean irw,
    boolean psww,
    int pswsel) {
        this.cond = cond;
        this.targetState = targetState;
        this.aluop = aluop;
        this.src1s = src1s;
        this.src2s = src2s;
        this.k = k;
        this.moe = moe;
        this.mwr = mwr;
        this.doe = doe;
        this.aoe = aoe;
        this.asel = asel;
        this.aw = aw;
        this.pcw = pcw;
        this.irw = irw;
        this.psww = psww;
        this.pswsel = pswsel;
    }

    AccMicroinstr() {
        super();
    }
    
    public int parse(String line) {
        String[] token = line.split("  *");
        int idx = 0;
        if (token[0].equals("")) {
            idx++;
        }
        int state = Integer.parseInt(token[idx++]);
        cond = Microinstruction.Condition.valueOf(token[idx++]);
        targetState = Integer.parseInt(token[idx++]);
        aluop = AluOp.valueOf(token[idx++]);
        src1s = Integer.parseInt(token[idx++]); 
        src2s = Integer.parseInt(token[idx++]);
        k = Short.parseShort(token[idx++]);
        moe = !token[idx++].equals("0");
        mwr = !token[idx++].equals("0");
        doe = !token[idx++].equals("0");
        aoe = !token[idx++].equals("0");
        asel = Integer.parseInt(token[idx++]);
        aw =  !token[idx++].equals("0");
        pcw = !token[idx++].equals("0");
        irw = !token[idx++].equals("0");
        psww = !token[idx++].equals("0");
        pswsel = Integer.parseInt(token[idx++]);
        return state;
    } 
   
}
