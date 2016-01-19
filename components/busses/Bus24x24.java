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

public class Bus24x24 extends SystemBus {
    public Bus24x24() {
        super(24,24);
    }
    
    public void write(int address, int data, int attributes) throws Exception {
        busWrite((long)address, (long)data, attributes);
    }
    
    public int read(int address, int attributes) throws Exception {
        return (int)busRead((long)address, attributes);
    }
}
