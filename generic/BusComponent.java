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

public abstract class BusComponent {
    
    protected SystemBus bus;
            
    public BusComponent() {
    }
    
    protected void setBus(SystemBus bus) {
        this.bus = bus;
    }
    
 
    public void onWrite(long address, long data, int attributes) 
            throws Exception {        
    }
    
    public long onRead(long address, int attributes) 
            throws Exception {
        return -1; 
    }
    
    public void onReadSnoop(long address, long data, int attributes) {        
    }
    
    public void onWriteSnoop(long address, long data, int attributes) {        
    }
    
    protected abstract boolean isAddressDecoded(long address, int attributes);
    protected abstract boolean isSnooping(long address, int attributes);
    abstract protected boolean isCompatible(SystemBus bus);
}
