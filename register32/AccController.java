/**
 * @Author M.K.
 */

package register32;

import java.util.logging.Logger;

import components.alus.Alu32;
import components.busses.Bus32x32;
import components.regfiles.RegFile32;
import components.registers.Reg32;

import generic.Alu.AluOp;
import generic.Microcode;
import generic.Microinstruction;
import generic.MicroprogController;
import generic.PSW;
import generic.SystemBus;

public class AccController extends MicroprogController {
    
	/* Busses */
    Bus32x32 systemBus;   
	
    private static Logger logger = Logger.getLogger(AccController.class.getName());
    
    /* Data paths */
    private int ab; // adresová sb�rnice
    private int db; 
    private int aluo;
    
    private int drd;

    /* Registers */
    private Reg32 PC = new Reg32();
    private Reg32 IR = new Reg32();
    
    private RegFile32 Register = new RegFile32(32);

    /* Functional Blocks */
    private final Alu32 alu = new Alu32();
    
    private AccPSW psw = new AccPSW(); // flags
    
    public Reg32 getProgramCounter(){
    	return PC;
    }
    public Reg32 getIR(){
    	return IR;
    }
	
    /**
     * Constructor
     * @param microcode
     * @param psw
     */
	public AccController(Microcode microcode, PSW psw, Bus32x32 systemBus) {
		super(microcode, psw);
		this.systemBus = systemBus;
		// TODO Auto-generated constructor stub
		// test
		Register.write(1, (int)15);
		Register.write(2, (int)2);
		//PC.setD((short)1);
		//PC.clock();
		
	}

	@Override
	protected void onLogic(Microinstruction mi) {
		
        AccMicroinstr m = (AccMicroinstr) mi;
        int op1 = 0, op2 = 0;
        int drm = 0;
        int drn = 0;
        int pca = 0;
        int pcb = 0;
        int pcin = 0;
        
        
        // adresová sběrnice
        if(m.aboe){
        	 if(m.asel == 0){
        		 ab = PC.getQ();
        	 }
        	 if(m.asel == 1){
        		 ab = IR.getQ();
        	 }
        	 if(m.asel == 2){
        		 ab = aluo;
        	 }
        }
        // register files
        drm = Register.read(m.rm);
        drn = Register.read(m.rn);
        
        //ab = (short) ((!m.aoe) ? 0xFFFF : (m.asel == 0) ? IR.getQ() : (m.asel == 1) ? PC.getQ() : 0x10);
        
        // otevřený výstup z memory
        if (m.moe) {
        	// otevřený výstup i z dboe = chyba
            if (m.dboe) {
                java.lang.System.out.printf(
                        "Error: bus conflict on DB (moe and dboe active simultaneously)\n");
                java.lang.System.exit(1);
            }


            try {
                db = systemBus.read(ab, SystemBus.M_16); // načtení dat na datovou sběrnici
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
        // načtení prvního operandu
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
                op2 = IR.getQ(); // ignorujeme extender
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
        
        if(m.dboe){
        	db = drd;
        }  
        
        // pcas
        switch(m.pcas){
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
        		pca = IR.getQ(); // ignorujeme extender
        		break;
        }
        // pcb
        if(m.pcbs == 1){
        	pcb = PC.getQ();
        }else{
        	pcb = 0;
        }
        //System.out.println(pcb);
        // PCIN
        pcin = (short)(pca + pcb);
        //System.out.println(pcin);
        // znovu po zpracování aluo?
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
	 * Při vzestupné hraně
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
        
        // zapsání do registru
        if(m.regw){
        	Register.write(m.rd, drd);
        } 
        // zapsání memory
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
		
		java.lang.System.out.printf("Instruction code [%04x]\n", (int) IR.getQ());
		
		// vezmeme pamět z instrukce (? co je to & 0xFFFF)
	    switch (((int) IR.getQ() & 0xFFFF)) {
	        // NOP
	        case 0x0000: 
	            return 3; // instrukce začíná na 3 řádku
	        // ADD rd, rn
	        case 0x4000:
	            return 8; // instrukce začíná na 10 řádku
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
            System.out.printf("%1d     ", m.src1s);
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
        System.out.println();
    }	

    public int extender(int instruction, int extop){
    	
    	// TODO
    	
		return instruction;
    	
    }
}
