/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components.io;

/**
 *
 * @author skrbek
 */
public interface IoInterf16x32 {
    public void iow(short address, int data, int attributes) throws Exception;
    public int ior(short address, int attributes) throws Exception;        
}
