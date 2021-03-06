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
public class Bus16x8 extends SystemBus {
    public Bus16x8() {
        super(16,8);
    }
    
    public void write(short address, byte data, int attributes) throws Exception {
        busWrite((long)address, (long)data, attributes);
    }
    
    public byte read(short address, int attributes) throws Exception {
        return (byte)(busRead((long)address, attributes));
    }
}
