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

/**
 *
 * @author skrbek
 */
public class Bus64x32 extends SystemBus {

    public Bus64x32() {
        super(64, 32);
    }

    public void write(long address, int data, int attributes) throws Exception {
        busWrite((long) address, (long) data, attributes);
    }

    public int read(long address, int attributes) throws Exception {
        return (int)busRead((long)address, attributes);
    }
    
}
