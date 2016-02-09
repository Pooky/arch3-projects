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
        // IF nacpání do adresové sběrnice
        "0  NEXT            0    NOP   0     0     0   0   0  0  0  0    0    1    1    0     1    0   0    0    1  ",
        // vložení do IR
        "1  NEXT            0    NOP   0     0     1   0   0  0  0  0    0    0    0    0     0    1   0    0    0   ",
        // zvýšení program counteru
        "2  NEXT            0    NOP   0     0     0   0   0  0  0  0    1    0    0    1     1    0   0    1    1   ",
        "3  SKIP_BY_DECODER 0    NOP   0     0     0   0   0  0  0  0    0    0    0    0     0    0   0    0    0   ",
        //// ADD rd, rn //////////////////////////////////////////////////////////////////////////////  
        // Execute
        "8  NEXT            0    ADD   0     0     0   0   0  1  2  0    0    0    0    0     0    0   0    0    0   ",
        // WRITE BACK
        "9  NEXT            0    NOP   0     0     0   0   1  0  0  1    0    0    0    2     0    0   0    0    0   ",
        // Increase counter AND SKIP
        "10  SKIP           0    NOP   0     0     0   0   0  0  0  0    1    0    0    1     1    0   0    1    1   ",
        // HALT
        "10  HALT           0   NOP   0     0     0   0   0  0  0  0    0    0    0    0     0    0   0    0    0   "
    
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
