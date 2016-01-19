/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components.busses;

import components.io.IoInterf16x32;
import components.memories.MemInterf32x32;

/**
 *
 * @author skrbek
 */
public class PC32SystemBus extends Bus32x32 implements MemInterf32x32, IoInterf16x32 {

    @Override
    public void memw(int address, int data, int attributes) throws Exception {
        write(address, data, attributes | MEM);
    }

    @Override
    public int memr(int address, int attributes) throws Exception {
        return read(address, attributes | MEM);
    }

    @Override
    public void iow(short address, int data, int attributes) throws Exception {
        write(address, data, attributes | IO);
    }

    @Override
    public int ior(short address, int attributes) throws Exception {
        return read(address, attributes | IO);
    }
    
}
