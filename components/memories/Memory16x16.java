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

package components.memories;

import generic.Memory;

public class Memory16x16 extends Memory implements MemInterf16x16 {
    
    public Memory16x16(short baseAddress, int size) {
        super((long)baseAddress & 0xFFFFL, size, 16, 2);
    }
    
    @Override
    public void memw(short address, short data, int attributes) throws Exception {
        onWrite((long)address & 0xFFFFL, (long)data & 0xFFFFL, attributes);
    }
    
    @Override
    public short memr(short address, int attributes) throws Exception {
        return (short)(super.onRead((long)address & 0xFFFFL, 
                attributes) & 0xFFFF);
    }
}
