/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components.memories;

/**
 *
 * @author skrbek
 */
public interface MemInterf64x32 {

    public void memw(long address, int data, int attributes) throws Exception;
    public int memr(long address, int attributes) throws Exception;
}
