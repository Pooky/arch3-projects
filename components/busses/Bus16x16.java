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

public class Bus16x16 extends SystemBus {
    public Bus16x16() {
        super(16,16);
    }
    
    public void write(short address, short data, int attributes) throws Exception {
        busWrite((long)address, (long)data, attributes);
    }
    
    public short read(short address, int attributes) throws Exception {
        return (short)busRead((long)address, attributes);
    }
}