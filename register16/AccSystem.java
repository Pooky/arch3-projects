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

import components.busses.Bus16x16;
import components.memories.Memory16x16;
import components.peripherals.BasicIO16x16;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccSystem {
	
	private static Logger logger = Logger.getLogger(AccSystem.class.getName());
	
    private final Memory16x16 mem;
    private final Bus16x16 systemBus;
    private final BasicIO16x16 io;
    private final AccCore core;

    public AccSystem() throws Exception {
        mem = new Memory16x16((short) 0, 4096);
        io = new BasicIO16x16((short)0x100);
        systemBus = new Bus16x16();
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
            
            system.mem.loadFromBinaryFile("C:\\Users\\Knot\\workspace\\arch\\src\\sources\\instrukce2.bin");
            //system.mem.loadFromBinaryFile(abso_path + "source.bin");
            //system.mem.loadFromBinaryFile(abso_path + "instrukce.bin");
            logger.info("Core run!");
            system.core.run();
            
            
        } catch (Exception ex) {
            Logger.getLogger(AccSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
