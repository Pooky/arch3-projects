
package components.memories;

public interface MemInterf24x24 {
    public void memw(int address, int data, int attributes) throws Exception;
    public int memr(int address, int attributes) throws Exception;    
}
