
package components.memories;

import generic.Memory;

 public class Memory64x64 extends Memory implements MemInterf64x64 {
    
    public Memory64x64(long baseAddress, int size) {
        super(baseAddress, size, 64, 8);
    }

    @Override
    public void memw(long address, long data, int attributes) throws Exception {
        super.onWrite(address, data, attributes);
    }

    @Override
    public long memr(long address, int attributes) throws Exception {
        return super.onRead(address, attributes);
    }
    
}