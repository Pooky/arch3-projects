
package components.memories;

public interface MemInterf16x8 {
    public void memw(short address, byte data, int attributes) throws Exception;
    public byte memr(short address, int attributes) throws Exception;    
}
