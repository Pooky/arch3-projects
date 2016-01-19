
package components.memories;

public interface MemInterf24x16 {
    public void memw(int address, short data, int attributes) throws Exception;
    public short memr(int address, int attributes) throws Exception;    
}
