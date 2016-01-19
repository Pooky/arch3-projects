/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package components.memories;

import generic.Memory;

public class Memory24x8 extends Memory implements MemInterf24x8 {
    
    public Memory24x8(short baseAddress, int size) {
        super((long)baseAddress & 0xFFFFFFL, size, 24, 1);
    }
    
    @Override
    public void memw(int address, byte data, int attributes) throws Exception {
        onWrite((long)address & 0xFFFFFFL, (long)data & 0xFFFFL, attributes);
    }
    
    @Override
    public byte memr(int address, int attributes) throws Exception {
        return (byte)(super.onRead((long)address & 0xFFFFFFL, 
                attributes) & 0xFFFF);
    }
}