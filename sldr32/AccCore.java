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
        // state  cond skip-to-state aluop src1s src2s k moe mwr doe aoe asel aw pcw irw psww pswsel dboe regw aboe pcwr rd rm m extop psel rdsel pcas pcbs
        // IF
        "  0      HALT             0   NOP     0     0 0   0   0   0   1   1   0   0   0    0      0    0    0    0    0  0  0 0     0    0     0    0    0",

    };

   
    AccCore(Bus16x16 systemBus) {
    	this();
        this.systemBus = systemBus;
        
    }

    AccCore() {
    	this.microcode = new AccMicrocode(mc);
    	this.controller = new AccController(microcode, new AccPSW(), systemBus);   	
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
        	logger.info("Clock (bíp)");
        	clock();
            
        }
        logger.info("Everything done. I quit!");
    }

}
