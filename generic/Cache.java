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

public abstract class Cache extends BusComponent {
        
    public Cache(SystemBus bus) throws Exception {
        super();
        bus.connect(this);
    }
    
    @Override
    protected boolean isSnooping(long address, int attribute) {
       return true; 
    }
    
    @Override
    protected boolean isAddressDecoded(long address, int attribute) {
        return false;
    }
    
}
