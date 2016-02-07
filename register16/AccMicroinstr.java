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
package register16;

import java.util.Arrays;

import generic.Alu.AluOp;
import generic.Microinstruction;
/* v�pis ��d�c�ch sign�l� z registrov� architektury
 * 
 * moe - implementov�no
 * mwr - implementov�no
 * psww - implementov�no
 * psel - implementov�no LP
 * dboe - implementov�no LP
 * rd - implementov�no LP
 * rm - implementov�no LP
 * m - implementov�no LP
 * regw - implementov�no LP
 * src1s - implementov�no
 * src2s - implementov�no
 * rdsel - implementov�no LP
 * aluop - implementov�no
 * aboe - implementov�no LP
 * asel - implementov�no
 * pcas  - implementov�no LP
 * irw - implementov�no
 * extop - implementov�no LP
 * pcbs  - implementov�no LP
 * pcwr - implementov�no LP
 * */

public class AccMicroinstr extends Microinstruction {
    AluOp aluop;
    int src1s;
    int src2s;
    boolean moe;
    boolean mwr;

    int asel;
    boolean irw;
    boolean psww;
    
    boolean dboe;
    boolean regw;
    boolean aboe;
    boolean pcwr;
    
    boolean rd;
    boolean rm;
	boolean rn;
    
    int rdsel;
    int pcas;
    int pcbs;

    
    public AccMicroinstr(
    Microinstruction.Condition cond,
    int targetState,        
    AluOp aluop,
    int src1s,
    int src2s,
 
    boolean moe,
    boolean mwr,
    boolean doe,
    boolean aoe,
    int asel,
    boolean aw,
    boolean pcw,
    boolean irw,
    boolean psww,
    boolean dboe,
    boolean regw,
    boolean aboe,
    boolean pcwr,
    int rd,
    int rm,
    boolean m,
    boolean extop,
    int psel,
    int rdsel,
    int pcas,
    int pcbs,
    int pswsel) {
        this.cond = cond;
        this.targetState = targetState;
        this.aluop = aluop;
        this.src1s = src1s;
        this.src2s = src2s;
        this.moe = moe;
        this.mwr = mwr;
        this.asel = asel;
        this.irw = irw;
        this.psww = psww;
        this.dboe = dboe;
        this.regw = regw;
        this.aboe = aboe;
        this.pcwr = pcwr;
        this.rm = rn;
        this.rdsel = rdsel;
        this.pcas = pcas;
        this.pcbs = pcbs;
    }

    AccMicroinstr() {
        super();
    }
    
    public int parse(String line) {
        String[] token = line.split("  *");
        
        System.out.println(Arrays.toString(token));
        System.out.println(token.length);
        //state  cond skip/next aluop src1s src2s moe mwr rd rm rn regw dboe aboe asel rdsel pcwr irw psww pcas pcbs
        
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
        moe = !token[idx++].equals("0");
        mwr = !token[idx++].equals("0");
        rd = !token[idx++].equals("0");
        rm = !token[idx++].equals("0");
        rn = !token[idx++].equals("0");
        regw = !token[idx++].equals("0");
        dboe = !token[idx++].equals("0");
        aboe = !token[idx++].equals("0");
        asel = Integer.parseInt(token[idx++]);
        rdsel = Integer.parseInt(token[idx++]);
        pcwr = !token[idx++].equals("0");
        irw = !token[idx++].equals("0");
        psww = !token[idx++].equals("0");
        pcas = Integer.parseInt(token[idx++]);
        pcbs = Integer.parseInt(token[idx++]);
        
        return state;
    } 
   
}
