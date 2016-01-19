/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package components.memories;

import generic.Memory;

public class Memory64x32 extends Memory implements MemInterf64x32 {
    
    public Memory64x32(long baseAddress, int size) {
        super(baseAddress, size, 64, 4);
    }

    @Override
    public void memw(long address, int data, int attributes) throws Exception {
        super.onWrite(address, data, attributes);
    }

    
    @Override
    public int memr(long address, int attributes) throws Exception {
        return (int)super.onRead(address, attributes);
    }
    
}