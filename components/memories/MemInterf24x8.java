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
public interface MemInterf24x8 {
    public void memw(int address, byte data, int attributes) throws Exception;
    public byte memr(int address, int attributes) throws Exception;    
}
