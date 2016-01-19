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
package components.mmus;

import components.memories.MemInterf32x32;

public class DummyMmu32x32 implements MemInterf32x32 {
    
    private MemInterf32x32 subComp;
    
    public DummyMmu32x32(MemInterf32x32 subComp) {
        super();
        this.subComp = subComp;
    }
    
    @Override
    public void memw(int address, int data, int attributes) throws Exception {
        subComp.memw(address, data, attributes);
    }

    @Override
    public int memr(int address, int attributes) throws Exception {
        return subComp.memr(address, attributes);
    }
    
}
