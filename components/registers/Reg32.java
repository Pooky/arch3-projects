/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package components.registers;

import generic.Reg;

/**
 *
 * @author skrbek
 */
public class Reg32 extends Reg {
    private int in;
    private int out;
    
    public int getQ() {
        return out;
    }
    
    public void setD(int input) {
        in = input;
    }
    
    @Override
    public void clock() {
        out = in;
    }    
}