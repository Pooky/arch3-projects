/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package components.memories;

import generic.Memory;

public class Memory24x16 extends Memory implements MemInterf24x16 {
    
    public Memory24x16(int baseAddress, int size) {
        super((long)baseAddress & 0xFFFFFFL, size, 24, 2);
    }
    
    @Override
    public void memw(int address, short data, int attributes) throws Exception {
        onWrite((long)address & 0xFFFFFFL, (long)data & 0xFFFFL, attributes);
    }
    
    @Override
    public short memr(int address, int attributes) throws Exception {
        return (short)(super.onRead((long)address & 0xFFFFFFL, 
                attributes) & 0xFFFF);
    }
}