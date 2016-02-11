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
package register32;

import components.busses.Bus32x32;
import components.memories.Memory32x32;
import components.peripherals.BasicIO32x32;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccSystem {
	
	private static Logger logger = Logger.getLogger(AccSystem.class.getName());
	
    private final Memory32x32 mem;
    private final Bus32x32 systemBus;
    private final BasicIO32x32 io;
    private final AccCore core;

    public AccSystem() throws Exception {
        mem = new Memory32x32((short) 0, 4096);
        io = new BasicIO32x32((short)0x100);
        systemBus = new Bus32x32();
        systemBus.connect(mem);
        systemBus.connect(io);
        core = new AccCore(systemBus);
        
    }

    public static void main(String[] args) throws IOException {
        try {
            AccSystem system = new AccSystem();
            
          	logger.info("-----------------------------------------");
            logger.info("\t#### Starting system.... ####");
            logger.info("Loading memory dump...");
            
            system.mem.loadFromBinaryFile("C://Users//Knot//workspace//arch//bin//output.bin");
            logger.info("Core run!");
            system.core.run();
            
            
        } catch (Exception ex) {
            Logger.getLogger(AccSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
