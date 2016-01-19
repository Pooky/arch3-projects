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
package generic;

import java.util.ArrayList;

public class SystemBus {

    private final int addrWidth;
    private final int dataWidth;

    public static final int M_NONE = 0;   // not specified (default)
    public static final int M_8 = 0x00000001;   // 8-bits
    public static final int M_16 = 0x00000002;   // 16-bits
    public static final int M_24 = 0x00000003;   // 16-bits
    public static final int M_32 = 0x00000004;   // 32-bits
    public static final int M_64 = 0x00000008;   // 64-bits
    public static final int IO_8 = 0x00000011;
    public static final int IO_16 = 0x00000012;
    public static final int IO_32 = 0x00000014;
    public static final int IO_64 = 0x00000018;
    public static final int CFG_64 = 0x00000020;
    public static final int CMD = 0x000000F0;
    public static final int MEM = 0x00000000;
    public static final int IO = 0x00000010;
    public static final int CFG = 0x00000020;
    public static final int M_SIZE_MASK = 0x0000000F;
    public static final int M_LOCK = 0x80000000;  // start atomic cycle
    public static final int M_UNLOCK = 0x40000000; // end atomic cycle     
    public static final int M_BIG_ENDIAN = 0x20000000;

    protected final ArrayList<BusComponent> devices = new ArrayList<>();

    public SystemBus(int addrWidthInBits, int dataWithInBits) {
        super();
        addrWidth = addrWidthInBits;
        dataWidth = dataWithInBits;
    }

    public void connect(BusComponent device) throws Exception {
        if (device.isCompatible(this)) {
            device.setBus(this);
            devices.add(device);
        } else {
            throw new Exception("Device is not compatible to bus");
        }
    }

    public void disconnect(BusComponent device) {
        devices.remove(device);
    }

    protected void busWrite(long address, long data, int attributes) throws Exception {
        int count = 0;
        address = address & ~(-1L << addrWidth);
        data = data & ~(-1L << dataWidth);

        for (BusComponent dev : devices) {
            if (dev.isAddressDecoded(address, attributes)) {
                dev.onWrite(address, data, attributes);
                count++;
            } 
            if (dev.isSnooping(address, attributes)) {
                dev.onWriteSnoop(address, data, attributes);
            }
        }

        if (count > 1) {
            throw new Exception("address conflict on the bus");
        }
    }

    protected long busRead(long address, int attributes) throws Exception {
        int count = 0;
        address = address & ~(-1L << addrWidth);
        int shift = (attributes & SystemBus.M_SIZE_MASK) * 8;
        long data = (shift == 64) ? -1L : ~(-1L << shift);
        for (BusComponent dev : devices) {
            BusComponent bc = (BusComponent) dev;
            if (bc.isAddressDecoded(address, attributes)) {
                data = (long) bc.onRead(address, attributes);
                count++;
            }    
        }
        if (count > 1) {
            throw new Exception("address conflict on the bus");
        }
        for (BusComponent dev : devices) {  
            if (dev.isSnooping(address, attributes)) {
                dev.onReadSnoop(address, data, attributes);
            }
        }
        return data & ~(-1L << dataWidth);
    }

    public int addrWidthInBits() {
        return addrWidth;
    }

    public int dataWidthInBits() {
        return dataWidth;

    }
}
