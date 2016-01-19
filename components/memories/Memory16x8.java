/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package components.memories;

import generic.Memory;

public class Memory16x8 extends Memory implements MemInterf16x8 {
    
    public Memory16x8(short baseAddress, int size) {
        super((long)baseAddress & 0xFFFFL, size, 16, 1);
    }
    
    public void memw(short address, byte data, int attributes) throws Exception {
        onWrite((long)address & 0xFFFFL, (long)data & 0xFFL, attributes);
    }
    
    public byte memr(short address, int attributes) throws Exception {
        return (byte)(super.onRead((long)address & 0xFFFFL, 
                attributes) & 0xFF);
    }
}