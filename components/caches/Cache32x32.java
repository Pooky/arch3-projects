
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
package components.caches;

import components.busses.Bus32x32;
import components.memories.MemInterf32x32;
import generic.Cache;
import generic.SystemBus;

public class Cache32x32  extends Cache implements MemInterf32x32 {

    public Cache32x32(Bus32x32 bus) throws Exception {
        super(bus);
    }

    @Override
    public void memw(int address, int data, int attributes) throws Exception {
       // ((Bus32x32)this.bus).write(address, data, attributes);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int memr(int address, int attributes) throws Exception {
      //  return ((Bus32x32)this.bus).read(address, attributes);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected boolean isCompatible(SystemBus bus) {
        return bus.addrWidthInBits() == 32 && bus.dataWidthInBits() == 32;
    }
    
}
