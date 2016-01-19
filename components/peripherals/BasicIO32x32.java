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

package components.peripherals;

import components.busses.Bus32x32;
import generic.SystemBus;
import generic.BusComponent;

public class BasicIO32x32 extends BusComponent {

    private final long baseAddress;

    public BasicIO32x32(int baseAddress) {
        this.baseAddress = (long) baseAddress & 0xFFFFL;
    }

    @Override
    public void onWrite(long address, long data, int attributes) throws Exception {
        int offset = (int) (address - baseAddress);
        switch (offset) {
            // putchar
            case 0x00:
                System.out.printf("%c", data & 0xFF);
                break;
            // puts
            case 0x04:
                int v;
                do {
                    v = ((Bus32x32) this.bus).read((int)data, SystemBus.M_32);
                    if ((v & 0xFF) != 0) {
                        System.out.printf("%c", v & 0xFF);
                        v >>>= 8;
                        if ((v & 0xFF) != 0) {
                            System.out.printf("%c", v & 0xFF);
                        }
                    }
                } while ((v & 0xFF) != 0);
                break;
            // print int32 (dec)
            case 0x08:
                System.out.printf("%d", (int)data);
                break;
            // print int32 (hex)
            case 0x0C:
                System.out.printf("%04x", (int)data);
                break;
        }
    }

    @Override
    public long onRead(long address, int attributes) throws Exception {
        return 0xFFFFL;
    }

    @Override
    protected boolean isCompatible(SystemBus bus) {
        return bus.dataWidthInBits() == 32 && bus.dataWidthInBits() == 32;
    }

    @Override
    protected boolean isAddressDecoded(long address, int attributes) {
        return (address >= baseAddress && address < baseAddress + 16);
    }

    @Override
    protected boolean isSnooping(long address, int attributes) {
        return false;
    }

}
