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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Memory
        extends BusComponent {

    private byte[] content;
    private long baseAddress;
    private int width;
    private int addrWidth;

    public Memory(long baseAddress, int size, int addrWidthInBits,
            int dataWidthInBytes) {
        super();
        addrWidth = addrWidthInBits;
        this.baseAddress = baseAddress;
        width = dataWidthInBytes;
        content = new byte[size];
    }

    public void loadFromBinaryFile(String filename) {
        long fsz = (new File(filename)).length();
        if (fsz > content.length) {
            System.out.println("file is too large, out of built in memory");
        }
        FileInputStream f = null;
        try {
            f = new FileInputStream(filename);
            f.read(content);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                f.close();
            } catch (IOException ex) {
                Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void onWrite(long address, long data, int attributes) throws Exception {
        address &= ~(1L << addrWidth);
        long addrMask = width - 1;
        int offset = (int)((address - baseAddress)&0xFFFFFFFF); 
        int displacement = (int) (address & addrMask);
        int size = attributes & SystemBus.M_SIZE_MASK;
        if ((size + displacement) > width) {
            throw new Exception(String.format(
                    "Invalid memory operation [displ: %d + size: %d > width: %d]",
                    displacement, size, width));
        }
        address &= ~addrMask;
        boolean le = (attributes & SystemBus.M_BIG_ENDIAN) == 0;
        if (le) {
            data >>>= displacement * 8;
            for (int i = displacement; i < size; i++) {
                content[offset+i] = (byte) (data & 0xFF);
                data >>>= 8;
            }
        } else {
            data >>>= displacement * 8;
            for (int i = displacement + size - 1; i >= displacement; i--) {
                content[offset + i] = (byte) (data & 0xFF);
                data >>>= 8;
            }
        }
    }

    public long onRead(long address, int attributes) throws Exception {
        long data = 0;
        long addrMask = width - 1;
        int offset = (int)((address - baseAddress) & 0xFFFFFFFF);
        int displacement = (int) (address & addrMask);
        int size = attributes & SystemBus.M_SIZE_MASK;
        if ((size + displacement) > width) {
            System.out.println(
                String.format(
                    "Invalid memory operation [offset: %d + size: %d > width: %d]",
                    displacement, size, width));
            System.exit(1);
        }
        address &= ~addrMask;
        boolean le = (attributes & SystemBus.M_BIG_ENDIAN) == 0;
        if (le) {
            for (int i = displacement + size - 1; i >= displacement; i--) {
                data <<= 8;
                data |= content[offset + i] & 0xFFL;
            }
        } else {
            for (int i = displacement; i < size; i++) {
                data <<= 8;
                data |= content[offset + i];
            }
        }
        return data;
    }

    @Override
    public boolean isAddressDecoded(long address, int attributes) {
        return baseAddress <= address && address < baseAddress + content.length
            && (attributes & SystemBus.CMD) == SystemBus.MEM;
    }

    @Override 
    protected boolean isCompatible(SystemBus bus) {
        return bus.addrWidthInBits() == addrWidth &&
                bus.dataWidthInBits() == width * 8;
    }

    @Override
    protected boolean isSnooping(long address, int attributes) {
       return false;  
    }

    @Override
    public void onReadSnoop(long address, long data, int attributes) {
    }

    @Override
    public void onWriteSnoop(long address, long data, int attributes) {
    }

}
