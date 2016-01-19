/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components.regfiles;

public class RegFile16 {

    private final short[] data;

    public RegFile16(int size) {
        data = new short[size];
    }

    public short read(int reg) {
        return data[reg];
    }

    public void write(int reg, short data) {
        this.data[reg] = data;
    }

}
