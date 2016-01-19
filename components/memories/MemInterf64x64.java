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
public interface MemInterf64x64 {
     public void memw(long address, long data, int attributes) throws Exception;
    public long memr(long address, int attributes) throws Exception;    
}
