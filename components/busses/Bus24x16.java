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

public class Bus24x16 extends SystemBus {
    public Bus24x16() {
        super(24,16);
    }
    
    public void write(long address, short data, int attributes) throws Exception {
        busWrite((long)address, (long)data, attributes);
    }
    
    public short read(long address, int attributes) throws Exception {
        return (short)busRead(address, attributes);
    }
}
