/**
 * @Author M.K.
 */
package register16;

import java.util.logging.Logger;

import components.alus.Alu16;
import components.busses.Bus16x16;
import components.regfiles.RegFile16;
import components.registers.Reg16;

import generic.Alu.AluOp;
import generic.Microcode;
import generic.Microinstruction;
import generic.MicroprogController;
import generic.PSW;
import generic.SystemBus;

public class AccController extends MicroprogController {

    /* Busses */
    Bus16x16 systemBus;

    private static Logger logger = Logger.getLogger(AccController.class.getName());

    /* Data paths */
    private short ab; // adresovĂˇ sbÄ›rnice
    private short db;
    private short aluo;

    private short drd;
    
    private short rn;
    private short rm;

    /* Registers */
    private Reg16 PC = new Reg16();
    private Reg16 IR = new Reg16();

    private RegFile16 Register = new RegFile16(16);

    /* Functional Blocks */
    private final Alu16 alu = new Alu16();

    private AccPSW psw = new AccPSW(); // flags

    public Reg16 getProgramCounter() {
        return PC;
    }

    public Reg16 getIR() {
        return IR;
    }

    /**
     * Constructor
     *
     * @param microcode
     * @param psw
     */
    public AccController(Microcode microcode, PSW psw, Bus16x16 systemBus) {
        super(microcode, psw);
        this.systemBus = systemBus;
            // TODO Auto-generated constructor stub
        // test
        Register.write(1, (short) 15);
        Register.write(2, (short) 2);
        Register.write(15, (short) 31);
            //PC.setD((short)1);
        //PC.clock();

    }

    @Override
    protected void onLogic(Microinstruction mi) {

        AccMicroinstr m = (AccMicroinstr) mi;
        short op1 = 0, op2 = 0;
        short drm = 0;
        short drn = 0;
        short pca = 0;
        short pcb = 0;
        short pcin = 0;
        //short irv = 0;

        // adresovĂˇ sbÄ›rnice
        if (m.aboe) {
            if (m.asel == 0) {
                ab = PC.getQ();
            }
            if (m.asel == 1) {
                ab = extender(IR.getQ(), m.extop);
            }
            if (m.asel == 2) {
                ab = aluo;
            }
        }
        // register files
        //drm = Register.read(m.rm);
        //drn = Register.read(m.rn);
        drm = Register.read(rm);
        drn = Register.read(rn);

        //ab = (short) ((!m.aoe) ? 0xFFFF : (m.asel == 0) ? IR.getQ() : (m.asel == 1) ? PC.getQ() : 0x10);
        // otevĹ™enĂ˝ vĂ˝stup z memory
        if (m.moe) {
            // otevĹ™enĂ˝ vĂ˝stup i z dboe = chyba
            if (m.dboe) {
                java.lang.System.out.printf(
                        "Error: bus conflict on DB (moe and dboe active simultaneously)\n");
                java.lang.System.exit(1);
            }

            try {
                db = systemBus.read(ab, SystemBus.M_16); // naÄŤtenĂ­ dat na datovou sbÄ›rnici
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println(ab);
                System.exit(1);
            }
        }
        // register drd
        switch (m.rdsel) {
            case 0:
                drd = db;
                break;
            case 1:
                drd = PC.getQ();
                break;
            case 2:
                drd = aluo;
                break;
        }
        // naÄŤtenĂ­ prvnĂ­ho operandu
        switch (m.src1s) {
            case 0:
                op1 = drm;
                break;
            case 1:
                op1 = psw.output();
                break;
        }

        switch (m.src2s) {
            case 0:
                op2 = drn;
                break;
            case 1:
                op2 = extender(IR.getQ(), m.extop);
                break;

        }
        //System.out.println(op1);

        aluo = alu.exec(m.aluop, op1, op2, aluo, psw);
        //System.out.println(aluo);

        if (m.dboe) {
            db = aluo;
        } else if (!m.moe) {
            db = (short) 0xFFFF;
        }

        if (m.dboe) {
            db = drd;
        }

        // pcas
        switch (m.pcas) {
            case 0:
                pca = 2; // konstanta
                break;
            case 1:
                pca = db;
                break;
            case 2:
                pca = aluo;
                break;
            case 3:
                pca = extender(IR.getQ(), m.extop);
                break;
        }
        // pcb
        if (m.pcbs == 1) {
            pcb = PC.getQ();
        } else {
            pcb = 0;
        }
        //System.out.println(pcb);
        // PCIN
        pcin = (short) (pca + pcb);
        //System.out.println(pcin);
        // znovu po zpracovĂˇnĂ­ aluo?
        if (m.asel == 2 && m.aboe) {
            ab = aluo;
        }

        // BUSes -> Registers
        IR.setD(db);
        PC.setD(pcin);

        if (getState() == 0) {
            print(true);
        }
        print(false);

    }

    /**
     * PĹ™i vzestupnĂ© hranÄ›
     */
    protected void onRisingClockEdge(Microinstruction mi) {

        AccMicroinstr m = (AccMicroinstr) mi;

        if (m.pcwr) {
            PC.clock();
        }
        if (m.irw) {
            IR.clock();
        }
        if (m.psww) {
            psw.clock();
        }

        // zapsĂˇnĂ­ do registru
        if (m.regw) {
            Register.write(m.rd, drd);
        }
        // zapsĂˇnĂ­ memory
        if (m.mwr) {
            try {
                systemBus.write(ab, db, SystemBus.M_16);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.exit(1);
            }
        }

    }

    @Override
    protected int onDecodeInstruction() {

        java.lang.System.out.printf("Instruction code [%04x]\n", (short) IR.getQ());

        switch (((short) IR.getQ() & 0xFFFF)) { // return = instrukce zaÄŤĂ­nĂˇ na X Ĺ™Ăˇdku
            // NOP
            case 0b0000_0000_0000_0000: // real 0000 0000 0000 0000
                return 2;

            // HALT
            case 0b1111_1111_1111_1111: // real 1111 1111 1111 1111
                return 9;
                
            // RET  ; PC = r15
            case 0b1111_0000_1100_1111: // real 1111 0000 1100 1111
                return 26;

            // RETI  ; PC = r15
            case 0b0100_0000_0000_0000:
                //case 0b1111_0001_1100_1111: // real 1111 0001 1100 1111
                return 27;

        }
        
        switch (((short) IR.getQ() & 0xF0F0)) { // return = instrukce zaÄŤĂ­nĂˇ na X Ĺ™Ăˇdku
            // ADD rd, rn ; rm += rn
            case 0b0000_0000_0010_0000: // real 0000 dddd 0010 nnnn
                rm = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 2);
                rn = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 4);
                return 3;

            // SUB rd, rn ; rm -= rn
            case 0b0000_0000_0100_0000: // real 0000 dddd 0010 nnnn
                rm = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 2);
                rn = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 4);
                return 6;
                
            // NEG rd, rn  ; rm = -rn
            case 0b0000_0000_0001_0000: // real 0000 dddd 0001 nnnn
                rm = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 2);
                rn = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 4);
                return 10;

            // AND rd, rn  ; rm = rm & rn
            case 0b0000_0000_1000_0000: // real 0000 dddd 1000 nnnn
                rm = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 2);
                rn = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 4);
                return 13;

            // OR rd, rn  ; rm = rm | rn
            case 0b0000_0000_1010_0000: // real 0000 dddd 1010 nnnn
                rm = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 2);
                rn = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 4);
                return 16;

            // XOR rd, rn  ; rm = rm ^ rn
            case 0b0000_0000_1100_0000: // real 0000 dddd 1100 nnnn
                rm = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 2);
                rn = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 4);
                return 19;

            // NOT rd, rn  ; rm = ~rn
            case 0b0000_0000_1110_0000: // real 0000 dddd 1110 nnnn
                rm = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 2);
                rn = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 4);
                return 22;

            // JMP [rn]  ; PC = rm
            case 0b1111_0000_1100_0000: // real 1111 0000 1100 nnnn
                rn = getRegistrNumberFromInstruction((short) IR.getQ(), (short) 4);
                return 25;

            

//            // LLDI rd, I rd‹I
//            case 0b1110_0000_0000_0000:
//                return 50;
//                //case 0b1110_0000_0000_0000: // real 1110 0000 0000 0000        
//
//            // LJMP A	pc‹A
//            case 0b1110_0000_0001_0000:
//                return 60;
//                //case 0b1110_0000_0001_0000: // real 1110 0000 0001 0000        
//
//            //LCALL A	r15‹pc, pc‹A
//            case 0b1110_1111_0010_0000:
//                return 70;
//                //case 0b1110_1111_0010_0000: // real 1110 1111 0010 0000      
                
        }
        
        //neproslo to ani jednim switchem
        java.lang.System.out.printf("Error: unknown instruction code [%04x]\n", (int) IR.getQ());
        java.lang.System.exit(1);
        return 0;
    }

    @Override
    public void run() throws Exception {
		// TODO Auto-generated method stub

    }

    @Override
    public void cycle() throws Exception {
		// TODO Auto-generated method stub

    }

    @Override
    public void stop() {
		// TODO Auto-generated method stub

    }

    /**
     * SLDACC_PRINT This function prints all registers and control isgnals of
     * the processor.
     */
    void print(boolean header) {
        //state  cond skip/next aluop src1s src2s moe mwr rd rm rn regw dboe aboe asel rdsel pcwr irw psww pcas pcbs
        AccMicroinstr m = (AccMicroinstr) this.getCurrentMicroinstruction();

        if (header) {
            System.out.printf("# ");
        } else {
            System.out.printf("  ");
        }
        if (header) {
            System.out.printf("clk     ");
        } else {
            System.out.printf("%s  ", "^^\\__/");
        }
        if (header) {
            System.out.printf("state   ");
        } else {
            System.out.printf("[%4d]  ", this.getState());
        }
        if (header) {
            System.out.printf("PC    ");
        } else {
            System.out.printf("%04X  ", PC.getQ());
        }

        if (header) {
            System.out.printf("IR    ");
        } else {
            System.out.printf("%04X  ", IR.getQ());
        }
        if (header) {
            System.out.printf("PSW   ");
        } else {
            System.out.printf("%04X  ", psw.output());
        }
        if (header) {
            System.out.printf("PSW    ");
        } else {
            System.out.printf("%5s  ", psw.getFlagsAsString());
        }
        if (header) {
            System.out.printf("AB    ");
        } else {
            System.out.printf("%04X  ", ab);
        }
        if (header) {
            System.out.printf("DB    ");
        } else {
            System.out.printf("%04X  ", db);
        }
        if (header) {
            System.out.printf("ALUO  ");
        } else {
            System.out.printf("%04X  ", aluo);
        }
        if (header) {
            System.out.printf("aluop  ");
        } else {
            System.out.printf("%-5s  ", m.aluop.toString());
        }
        if (header) {
            System.out.printf("r1 \t");
        } else {
            System.out.printf("%1d \t", Register.read(1));
        }
        if (header) {
            System.out.printf("r2 \t");
        } else {
            System.out.printf("%1d \t", Register.read(2));
        }
        /*if (header) {
         System.out.printf("r \t");
         } else {
         System.out.printf("%1d \t", Register.read(2));
         }*/
        if (header) {
            System.out.printf("drd \t");
        } else {
            System.out.printf("%1d ", drd);
        }
        if (header) {
            System.out.printf("src1s ");
        } else {
            System.out.printf("    %1d     ", m.src1s);
        }
        if (header) {
            System.out.printf("src2s ");
        } else {
            System.out.printf("%1d     ", m.src2s);
        }
        if (header) {
            System.out.printf("moe ");
        } else {
            System.out.printf("%d   ", (m.moe) ? 1 : 0);
        }
        if (header) {
            System.out.printf("mwr ");
        } else {
            System.out.printf("%d   ", (m.mwr) ? 1 : 0);
        }
        if (header) {
            System.out.printf("asel ");
        } else {
            System.out.printf("%d    ", m.asel);
        }
        if (header) {
            System.out.printf("pcwr    ");
        } else {
            System.out.printf("%d      ", (m.pcwr) ? 1 : 0);
        }
        if (header) {
            System.out.printf("irw    ");
        } else {
            System.out.printf("%d      ", (m.irw) ? 1 : 0);
        }
        if (header) {
            System.out.printf("psww   ");
        } else {
            System.out.printf("%d      ", (m.psww) ? 1 : 0);
        }
        if (header) {
            System.out.printf("extop   ");
        } else {
            System.out.printf("%d      ", (m.extop));
        }
        if (header) {
            System.out.printf("EX v   ");
        } else {
            System.out.printf("%04X    ", extender(IR.getQ(), m.extop));
        }
        System.out.println();
    }

    public short extender(short instruction, int extop) {
        short tmp;
        switch (extop) {
            case 0b000: // xxxxxxxxxxxxuuuu -> 000000000000uuuu
                tmp = (short) (instruction & 0b0000_0000_0000_1111);
                return tmp;

            case 0b001: // xxxxxxxxxxxsvvvv -> ssssssssssssvvvv
                tmp = (short) (instruction & 0b0000_0000_0001_1111);
                tmp = (short) (tmp << 11);
                tmp = (short) (tmp >> 11);
                return tmp;

            case 0b010: // xxxxxxxxxxsvvvvv -> sssssssssssvvvvv
                tmp = (short) (instruction & 0b0000_0000_0011_1111);
                tmp = (short) (tmp << 10);
                tmp = (short) (tmp >> 10);
                return tmp;

            case 0b011: // xxxxxxxxsvvvvvvv -> sssssssssvvvvvvv
                tmp = (short) (instruction & 0b0000_0000_1111_1111);
                tmp = (short) (tmp << 8);
                tmp = (short) (tmp >> 8);
                return tmp;

            case 0b100: // xxxxxxxxuuuuuuuu -> 0000000uuuuuuuu0
                tmp = (short) (instruction & 0b0000_0000_1111_1111);
                tmp = (short) (tmp << 1);
                return tmp;

            case 0b101: // xxxxxxxxsvvvvvvv -> ssssssssvvvvvvv0
                tmp = (short) (instruction & 0b0000_0000_1111_1111);
                tmp = (short) (tmp << 8);
                tmp = (short) (tmp >> 8);
                tmp = (short) (tmp << 1);
                return tmp;

            case 0b110: // xxxxuuuuuuuuuuuu -> 000uuuuuuuuuuuu0
                tmp = (short) (instruction & 0b0000_1111_1111_1111);
                tmp = (short) (tmp << 1);
                return tmp;

            case 0b111: // xxxxsvvvvvvvvvvv -> ssssvvvvvvvvvvv0
                tmp = (short) (instruction & 0b0000_1111_1111_1111);
                tmp = (short) (tmp << 4);
                tmp = (short) (tmp >> 4);
                tmp = (short) (tmp << 1);
                return tmp;
        }

        return instruction;

    }
    
    public short getRegistrNumberFromInstruction(short instruction, short whichQuadrant){
        short tmp;
        switch (whichQuadrant){
            case 1: // VVVV xxxx xxxx xxxx -> 0000 0000 0000 VVVV
                return (short) (instruction >>> 12);
            case 2: // xxxx VVVV xxxx xxxx -> 0000 0000 0000 VVVV
                tmp = instruction;
                tmp = (short) (tmp << 4);
                return (short) (tmp >>> 12);
            case 3: // xxxx xxxx VVVV xxxx -> 0000 0000 0000 VVVV
                tmp = instruction;
                tmp = (short) (tmp << 8);
                return (short) (tmp >>> 12);   
            case 4: // xxxx xxxx xxxx VVVV -> 0000 0000 0000 VVVV
                return (short) (instruction & 0b0000_0000_0000_1111);  
        }
        return (short) 0xFFFF;
    }
}