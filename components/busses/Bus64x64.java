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
package components.busses;

import generic.SystemBus;

public class Bus64x64 extends SystemBus {
    public Bus64x64() {
        super(64,64);
    }
    
    public void write(long address, long data, int attributes) throws Exception {
        busWrite((long) address, (long) data, attributes);
    }

    public long read(long address, int attributes) throws Exception {
        return busRead((long)address, attributes);
    }

}