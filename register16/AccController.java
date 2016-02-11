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
    private short ab; // adresová sběrnice
    private short db; 
    private short aluo;
    
    private short drd;

    /* Registers */
    private Reg16 PC = new Reg16();
    private Reg16 IR = new Reg16();
    
    private RegFile16 Register = new RegFile16(16);

    /* Functional Blocks */
    private final Alu16 alu = new Alu16();
    
    private AccPSW psw = new AccPSW(); // flags
    
    public Reg16 getProgramCounter(){
    	return PC;
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
		// test
		Register.write(1, (short)15);
		Register.write(2, (short)2);
                Register.write(15, (short)31);
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
		
		java.lang.System.out.printf("Instruction code [%04x]\n", (short) IR.getQ());
            
		// vezmeme pamět z instrukce (? co je to & 0xFFFF)
	    switch (((short) IR.getQ() & 0xFFFF)) { // return = instrukce začíná na X řádku
                //case 0b0100_0000_0000_0000: // 4000 to match the 1st instr in output.bin
                //case 0b1111_1111_1111_1111: // FFFF to match the 2nd instr in output.bin - halt
                
                // NOP
	        case 0b0000_0000_0000_0000: // real 0000 0000 0000 0000
	            return 2; 
                    
                // ADD rd, rn ; r1 += r2
                case 0b0000_0001_0010_0010: // real 0000 dddd 0010 nnnn
	            return 3;
                    
                // SUB rd, rn ; r1 -= r2
                case 0b0000_0001_0100_0010: // real 0000 dddd 0010 nnnn
	            return 6;
                
                // HALT
                case 0b1111_1111_1111_1111: // real 1111 1111 1111 1111
	            return 9;
                   
                // NEG rd, rn  ; r1 = -r2
                case 0b0000_0001_0001_0010: // real 0000 dddd 0001 nnnn
	            return 10;
                    
                // AND rd, rn  ; r1 = r1 & r2
                case 0b0000_0001_1000_0010: // real 0000 dddd 1000 nnnn
	            return 13;
                        
                // OR rd, rn  ; r1 = r1 | r2
                case 0b0000_0001_1010_0010: // real 0000 dddd 1010 nnnn
	            return 16;
                        
                // XOR rd, rn  ; r1 = r1 ^ r2
                case 0b0000_0001_1100_0010: // real 0000 dddd 1100 nnnn
	            return 19;
                    
                // NOT rd, rn  ; r1 = ~r2
                case 0b0000_0001_1110_0010: // real 0000 dddd 1110 nnnn
	            return 22;
                    
                // JMP [rn]  ; PC = r1
                case 0b1111_0000_1100_0001: // real 1111 0000 1100 nnnn
	            return 25;
                    
                // RET  ; PC = r15
                case 0b1111_0000_1100_1111: // real 1111 0000 1100 1111
	            return 26;
                    
                // RETI  ; PC = r15
                case 0b0100_0000_0000_0000:
                //case 0b1111_0001_1100_1111: // real 1111 0001 1100 1111
	            return 27;
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
//	        // LDA        
//	        case 0x8000:
//	            return 15;
//	        // ADD        
//	        case 0x0200:
//	            return 20;
//	        // CALL        
//	        case 0xc000:
//	            return 25;
//	        // RET
//	        case 0xc001:
//	            return 35;
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

    public short extender(short instruction, int extop){
    	short tmp;
    	switch (extop){
            case 0b000: // xxxxxxxxxxxxuuuu -> 000000000000uuuu
                tmp = (short)(instruction & 0b0000_0000_0000_1111);
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
}
