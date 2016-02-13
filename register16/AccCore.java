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

import java.util.logging.Logger;

import components.alus.Alu16;
import components.busses.Bus16x16;
import components.registers.Reg16;
import generic.SystemBus;
import generic.MicroprogController;
import generic.Core;
import generic.Microinstruction;

public class AccCore extends Core {
	   
	private static Logger logger = Logger.getLogger(AccCore.class.getName());
	
	/* Busses */
    Bus16x16 systemBus;   
		
	// controller
	private AccController controller;
	private AccMicrocode microcode;

    /* Microcode */
    String[] mc = new String[]{
        // state  cond skip/next aluop src1s src2s moe mwr rd rm rn regw dboe aboe asel rdsel pcwr irw psww pcas pcbs
        // IF + ID
        "0  NEXT            0    NOP   0     0     1   0   0  0  0  0    0    1    1    0     0    1   0    0    0",
        "1  SKIP_BY_DECODER 0    NOP   0     0     0   0   0  0  0  0    0    0    0    0     0    0   0    0    0",
  
        
        //// NOP //////////////////////////////////////////////////////////////////////////////  
        // EX        
        // WB
        // PC++ AND SKIP
        "2  SKIP           0    NOP   0     0     0   0   0  0  0  0    0    0    0    0     1    0   0    0    1",
                
        
        //ADD rd, rn //////////////////////////////////////////////////////////////////////////////  
        // EX        
        "3  NEXT            0    ADD   0     0     0   0   0  1  2  0    0    0    0    0     0    0   0    0    0",
        // WB
        "4  NEXT            0    NOP   0     0     0   0   1  0  0  1    0    0    0    2     0    0   0    0    0",
        // PC++ AND SKIP
        "5  SKIP           0    NOP   0     0     0   0   0  0  0  0    0    0    0    0     1    0   0    0    1",
        
        
        //SUB rd, rn //////////////////////////////////////////////////////////////////////////////  
        // EX        
        "6  NEXT            0    SUB   0     0     0   0   0  1  2  0    0    0    0    0     0    0   0    0    0",
        // WB
        "7  NEXT            0    NOP   0     0     0   0   1  0  0  1    0    0    0    2     0    0   0    0    0",
        // PC++ AND SKIP
        "8  SKIP           0    NOP   0     0     0   0   0  0  0  0    0    0    0    0     1    0   0    0    1",
        
        // HALT //////////////////////////////////////////////////////////////////////////////  
        "9  HALT           0   NOP   0     0     0   0   0  0  0  0    0    0    0    0     0    0   0    0    0",
            
        //NEG rd, rn //////////////////////////////////////////////////////////////////////////////  
        // EX        
        "10  NEXT            0    NEG   0     0     0   0   0  2  0  0    0    0    0    0     0    0   0    0    0",
        // WB
        "11  NEXT            0    NOP   0     0     0   0   1  0  0  1    0    0    0    2     0    0   0    0    0",
        // PC++ AND SKIP
        "12  SKIP           0    NOP   0     0     0   0   0  0  0  0    0    0    0    0     1    0   0    0    1",
        
        //AND rd, rn //////////////////////////////////////////////////////////////////////////////  
        // state  cond skip/next aluop src1s src2s moe mwr rd rm rn regw dboe aboe asel rdsel pcwr irw psww pcas pcbs
        // EX        
        "13  NEXT            0    AND   0     0     0   0   0  1  2  0    0    0    0    0     0    0   0    0    0",
        // WB
        "14  NEXT            0    NOP   0     0     0   0   1  0  0  1    0    0    0    2     0    0   0    0    0",
        // PC++ AND SKIP
        "15  SKIP           0    NOP   0     0     0   0   0  0  0  0    0    0    0    0     1    0   0    0    1",
        
        //OR rd, rn //////////////////////////////////////////////////////////////////////////////  
        // EX        
        "16  NEXT            0    OR   0     0     0   0   0  1  2  0    0    0    0    0     0    0   0    0    0",
        // WB
        "17  NEXT            0    NOP   0     0     0   0   1  0  0  1    0    0    0    2     0    0   0    0    0",
        // PC++ AND SKIP
        "18  SKIP           0    NOP   0     0     0   0   0  0  0  0    0    0    0    0     1    0   0    0    1",
        
        //XOR rd, rn //////////////////////////////////////////////////////////////////////////////  
        // EX        
        "19  NEXT            0    XOR   0     0     0   0   0  1  2  0    0    0    0    0     0    0   0    0    0",
        // WB
        "20  NEXT            0    NOP   0     0     0   0   1  0  0  1    0    0    0    2     0    0   0    0    0",
        // PC++ AND SKIP
        "21  SKIP           0    NOP   0     0     0   0   0  0  0  0    0    0    0    0     1    0   0    0    1",
        
        //NOT rd, rn //////////////////////////////////////////////////////////////////////////////  
        // EX        
        "22  NEXT            0    CPL   0     0     0   0   0  2  0  0    0    0    0    0     0    0   0    0    0",
        // WB
        "23  NEXT            0    NOP   0     0     0   0   1  0  0  1    0    0    0    2     0    0   0    0    0",
        // PC++ AND SKIP
        "24  SKIP           0    NOP   0     0     0   0   0  0  0  0    0    0    0    0     1    0   0    0    1",
        
        //JMP [rn] //////////////////////////////////////////////////////////////////////////////  
        // state  cond skip/next aluop src1s src2s moe mwr rd rm rn regw dboe aboe asel rdsel pcwr irw psww pcas pcbs
        // EX + WB        
        "25  SKIP            0    SRC2   0     0     0   0   0  0  1  0    0    0    0    0     1    0   0    2    0",
        
        //RET //////////////////////////////////////////////////////////////////////////////  
        // state  cond skip/next aluop src1s src2s moe mwr rd rm rn regw dboe aboe asel rdsel pcwr irw psww pcas pcbs
        // EX + WB       
        "26  SKIP            0    SRC2   0     0     0   0   0  0  15  0    0    0    0    0     1    0   0    2    0",
        
        //RETI //////////////////////////////////////////////////////////////////////////////  
        // state  cond skip/next aluop src1s src2s moe mwr rd rm rn regw dboe aboe asel rdsel pcwr irw psww pcas pcbs
        // EX + WB       
        "27  SKIP            0    SRC2   0     0     0   0   0  0  15  0    0    0    0    0     1    0   0    2    0",
        
        
        //// LLDI /////////////////////////////////////////////////////////////////////////////
        // EX
        "50 NEXT            0    LLDI  0     1     0   0   0  0  0  0    0    0    0    0     0    0   0    0    0",
        // WB
        "51 NEXT            0    NOP   0     1     0   0   1  0  0  1    0    0    0    2     0    0   0    0    0",
        // PC++ AND SKIP
        "52 SKIP            0    NOP   0     0     0   0   0  0  0  0    0    0    0    0     1    0   0    0    1",
        
    
            
    };

   
    AccCore(Bus16x16 systemBus) {
        this.systemBus = systemBus;
    	this.microcode = new AccMicrocode(mc);
    	this.controller = new AccController(microcode, new AccPSW(), systemBus);   	
    }

    AccCore() {

    }


    public MicroprogController getController() {
        return controller;
    }

    @Override
    public void clock() {
        controller.clock();
    }

    @Override
    public void cycle() {
        controller.step();
    }

    @Override
    public boolean isHalted() {
        return controller.isHalted();
    }

    @Override
    public void run() {
    	
    	logger.info("Running...");
        while (!isHalted()) {
        	//logger.info("Clock (bíp)");
        	clock();
        }
        logger.info("Everything done. I quit!");
    }

}
