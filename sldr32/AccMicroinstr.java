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
package sldr32;

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
    
    boolean dboe;
    boolean regw;
    boolean aboe;
    boolean pcwr;
    
    int rd;
    int rm;
    boolean m;
    boolean extop;
    
    int psel;
    int rdsel;
    int pcas;
    int pcbs;
	public int rn;
    
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
    boolean dboe,
    boolean regw,
    boolean aboe,
    boolean pcwr,
    int rd, // adresa zápisu
    int rm, // read m
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
        this.dboe = dboe;
        this.regw = regw;
        this.aboe = aboe;
        this.pcwr = pcwr;
        this.rd = rd;
        this.rm = rm;
        this.m = m;
        this.extop = extop;
        this.psel = psel;
        this.rdsel = rdsel;
        this.pcas = pcas;
        this.pcbs = pcbs;
    }

    AccMicroinstr() {
        super();
    }
    
    public int parse(String line) {
        String[] token = line.split("  *");
        
        //System.out.println(token.toString());
        //System.out.println(token.length);
        
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
        dboe = !token[idx++].equals("0");
        regw = !token[idx++].equals("0");
        aboe = !token[idx++].equals("0");
        pcwr = !token[idx++].equals("0");
        rd = Integer.parseInt(token[idx++])
        rm = Integer.parseInt(token[idx++])
        m = !token[idx++].equals("0");
        extop = !token[idx++].equals("0");
        psel = Integer.parseInt(token[idx++]);
        rdsel = Integer.parseInt(token[idx++]);
        pcas = Integer.parseInt(token[idx++]);
        pcbs = Integer.parseInt(token[idx++]);
        
        return state;
    } 
   
}
