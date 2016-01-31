package sldr32;

import components.alus.Alu16;
import components.busses.Bus16x16;
import components.registers.Reg16;

import generic.Microcode;
import generic.Microinstruction;
import generic.MicroprogController;
import generic.PSW;
import generic.SystemBus;

public class AccController extends MicroprogController {
    
	/* Busses */
    Bus16x16 systemBus;   
	
    /* Data paths */
    private short ab;
    private short db;
    private short aluo;

    /* Registers */
    private Reg16 PC = new Reg16();
    private Reg16 A = new Reg16();
    private Reg16 IR = new Reg16();

    /* Functional Blocks */
    private final Alu16 alu = new Alu16();
    
    private AccPSW psw = new AccPSW(); // flags
    
    public Reg16 getProgramCounter(){
    	return PC;
    }
    public Reg16 getA(){
    	return A;
    }
    public Reg16 getIR(){
    	return IR;
    }
	
    /**
     * Constructor
     * @param microcode
     * @param psw
     */
	public AccController(Microcode microcode, PSW psw, Bus16x16 systemBus) {
		super(microcode, psw);
		
		this.systemBus = systemBus;
		
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onLogic(Microinstruction mi) {
		
        AccMicroinstr m = (AccMicroinstr) mi;
        short op1 = 0, op2 = 0;

        ab = (short) ((!m.aoe) ? 0xFFFF : (m.asel == 0) ? IR.getQ()
                : (m.asel == 1) ? PC.getQ() : 0x10);

        if (m.moe) {
            if (!m.aoe) {
                java.lang.System.out.printf("Error: address bus in Z state\n");
                java.lang.System.exit(1);
            }
            if (m.doe) {
                java.lang.System.out.printf(
                        "Error: bus conflict on DB (moe and doe active simultaneously)\n");
                java.lang.System.exit(1);
            }

            try {
                db = systemBus.read(ab, SystemBus.M_16);
            } catch (Exception ex) {
              System.out.println(ex.getMessage());
              System.exit(1);
            }
        }

        switch (m.src1s) {
            case 0:
                op1 = IR.getQ();
                break;
            case 1:
                op1 = db;
                break;
            case 2:
                op1 = (short) m.k;
                break;
        }

        switch (m.src2s) {
            case 0:
                op2 = IR.getQ();
                break;
            case 1:
                op2 = PC.getQ();
                break;
            case 2:
                op2 = A.getQ();
                break;
            case 3:
                op2 = psw.output();
                break;
        }
        aluo = alu.exec(m.aluop, op1, op2, aluo, psw);

        if (m.asel == 2 && m.aoe) {
            ab = aluo;  
            
        }

        if (m.doe) {
            db = aluo;
        } else if (!m.moe) {
            db = (short) 0xFFFF;
        }

        // BUSes -> Registers
        PC.setD(aluo);
        IR.setD(db);
        A.setD(aluo);
        
        if (getState() == 0) {
            print(true);
        }
        print(false);
		
	}

	/**
	 * Při vzestupné hraně
	 */
	protected void onRisingClockEdge(Microinstruction m) {
		
        AccMicroinstr mi = (AccMicroinstr) getCurrentMicroinstruction();
        if (mi.pcw) {
            PC.clock();
        }
        if (mi.irw) {
            IR.clock();
        }
        if (mi.psww) {
            psw.clock();
        }
        if (mi.aw) {
            A.clock();
        }
        if (mi.mwr) {
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
		
		// vezmeme pamět z instrukce (? co je to & 0xFFFF)
	    switch (((int) IR.getQ() & 0xFFFF)) {
	        // NOP
	        case 0x0000: 
	            return 3; // instrukce začíná na 3 řádku
	        // JMP
	        case 0x4000:
	            return 10; // instrukce začíná na 10 řádku
	        // LDA        
	        case 0x8000:
	            return 15;
	        // ADD        
	        case 0x0200:
	            return 20;
	        // CALL        
	        case 0xc000:
	            return 25;
	        // RET
	        case 0xc001:
	            return 35;
	        default:
	            java.lang.System.out.printf("Error: unknown instruction code [%04x]\n", (int) IR.getQ());
	            java.lang.System.exit(1);
	    }
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
            System.out.printf("A     ");
        } else {
            System.out.printf("%04X  ", A.getQ());
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
            System.out.printf("src1s ");
        } else {
            System.out.printf("%1d     ", m.src1s);
        }
        if (header) {
            System.out.printf("src2s ");
        } else {
            System.out.printf("%1d     ", m.src2s);
        }
        if (header) {
            System.out.printf("k     ");
        } else {
            System.out.printf("%04X  ", m.k);
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
            System.out.printf("doe ");
        } else {
            System.out.printf("%d   ", (m.doe) ? 1 : 0);
        }
        if (header) {
            System.out.printf("aoe ");
        } else {
            System.out.printf("%d   ", (m.aoe) ? 1 : 0);
        }
        if (header) {
            System.out.printf("asel ");
        } else {
            System.out.printf("%d    ", m.asel);
        }
        if (header) {
            System.out.printf("aw ");
        } else {
            System.out.printf("%d  ", (m.aw) ? 1 : 0);
        }

        if (header) {
            System.out.printf("pcw    ");
        } else {
            System.out.printf("%d      ", (m.pcw) ? 1 : 0);
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
            System.out.printf("pswsel ");
        } else {
            System.out.printf("%d      ", m.pswsel);
        }
        System.out.println();
    }	

}
