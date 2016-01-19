
package components.memories;

public interface MemInterf16x16 {
    public void memw(short address, short data, int attributes) throws Exception;
    public short memr(short address, int attributes) throws Exception;       
}
