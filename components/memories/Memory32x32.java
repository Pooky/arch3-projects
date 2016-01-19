/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package components.memories;

import generic.Memory;

public class Memory32x32 extends Memory implements MemInterf32x32 {
    
    public Memory32x32(int baseAddress, int size) {
        super((long)baseAddress & 0xFFFFFFFFL, size, 32, 4);
    }
    
    @Override
    public void memw(int address, int data, int attributes) throws Exception {
        onWrite((long)address & 0xFFFFFFFFL, (long)data & 0xFFFFFFFFL, attributes);
    }
    
    @Override
    public int memr(int address, int attributes) throws Exception {
        return (int)(super.onRead((long)address & 0xFFFFFFFFL, 
                attributes) & 0xFFFFFFFFL);
    }
}