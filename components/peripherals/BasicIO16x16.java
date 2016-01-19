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

package components.peripherals;

import components.busses.Bus16x16;
import generic.SystemBus;
import generic.BusComponent;

public class BasicIO16x16 extends BusComponent {

    private final long baseAddress;

    public BasicIO16x16(short baseAddress) {
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
                short v;
                do {
                    v = ((Bus16x16) this.bus).read((short) data, SystemBus.M_16);
                    if ((v & 0xFF) != 0) {
                        System.out.printf("%c", v & 0xFF);
                        v >>>= 8;
                        if ((v & 0xFF) != 0) {
                            System.out.printf("%c", v & 0xFF);
                        }
                    }
                } while ((v & 0xFF) != 0);
                break;
            // print int16 (dec)
            case 0x08:
                System.out.printf("%d", (int)data);
                break;
            // print int16 (hex)
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
        return bus.dataWidthInBits() == 16 && bus.dataWidthInBits() == 16;
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
