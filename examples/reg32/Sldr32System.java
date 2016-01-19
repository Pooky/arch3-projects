
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

/*
System Diagram
---------------

+-------------------+
|Proc               |
|  +--------------+ |
|  |Core          | |
|  | +----------+ | |
|  | |   mmu    | | |
|  | +----------+ | |
|  |      /\      | |
|  +------||------+ |
|         \/        |
|  +--------------+ |         +----------+        +----------+
|  |    cache     | |         |   MEM    |        | BasicIO  |
|  +--------------+ |         +----------+        +----------+ 
|         /\        |              /\                  /\
+---------||--------+              ||                  ||
          \/          SystemBus    \/                  \/
    <============================================================>

*/
package examples.reg32;

import components.caches.Cache32x32;
import components.memories.Memory32x32;
import components.peripherals.BasicIO32x32;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sldr32System {
    private final Memory32x32 mem;
    private final Sldr32SystemBus systemBus;
    private final BasicIO32x32 io;
    private final Cache32x32 cache;
    private final Sldr32Core core;

    public Sldr32System() throws Exception {
         mem = new Memory32x32(0, 4096);
        io = new BasicIO32x32(0xFFFFFF00);
        systemBus = new Sldr32SystemBus();
        systemBus.connect(mem);
        systemBus.connect(io);
        cache = new Cache32x32(systemBus);
        core = new Sldr32Core(cache);
    }
    
        public static void main(String[] args) throws IOException {
        try {
            Sldr32System system = new Sldr32System();
            system.mem.loadFromBinaryFile("prog.bin");
            system.core.run();
        } catch (Exception ex) {
            Logger.getLogger(Sldr32System.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
