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
public interface MemInterf32x16 {
    public void memw(int address, short data, int attributes) throws Exception;
    public short memr(int address, int attributes) throws Exception;    

}
