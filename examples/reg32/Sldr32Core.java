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

package examples.reg32;

import components.memories.MemInterf32x32;
import components.mmus.DummyMmu32x32;
import components.regfiles.RegFile32;
import generic.Core;

public class Sldr32Core extends Core {

    private boolean halted = false;
    
    private MemInterf32x32 mmu;
    
    private RegFile32 reg = new RegFile32(16);
    
    public Sldr32Core(MemInterf32x32 systemBus) {
        super();
        mmu = new DummyMmu32x32(systemBus);
    } 
    
    @Override
    public void clock() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cycle() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isHalted() {
        return halted;
    }

    @Override
    public void run() throws Exception {
        while (!isHalted()) {
            clock();
        }
    }

}
