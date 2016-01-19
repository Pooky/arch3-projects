/*
 * *****************************************************************************
 *
 * Computer Architecture Courseware 1.0
 *
 * Miroslav Skrbek (C) 2013,2014, 2015
 *
 * Free to use, copy and modify for non-commercial, educational and research
 * purposes only.
 *
 * NO WARRANTY OF ANY KIND.
 *
 *****************************************************************************
 */

package examples.reg32;

import components.caches.Cache32x32;

public class Sldr32Proc {
    private final Sldr32Core core;
    private final Cache32x32 cache;
    
    public Sldr32Proc(Sldr32SystemBus bus) throws Exception {
        super();
        cache = new Cache32x32(bus);
        core = new Sldr32Core(cache);
    }
    
    public void run() throws Exception {
        core.run();
    }
    
    public void cycle() throws Exception {
        core.cycle();
    }
}

