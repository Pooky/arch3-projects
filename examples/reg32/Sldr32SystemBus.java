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

import components.busses.Bus32x32;
import components.memories.MemInterf32x32;

public class Sldr32SystemBus extends Bus32x32 implements MemInterf32x32 {

    @Override
    public void memw(int address, int data, int attributes) throws Exception {
        write(address, data, attributes);
    }

    @Override
    public int memr(int address, int attributes) throws Exception {
        return read(address, attributes);
    }
 
}
