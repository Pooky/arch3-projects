/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components.regfiles;

public class RegFile8 {

    private final byte[] data;

    public RegFile8(int size) {
        data = new byte[size];
    }

    public byte read(int reg) {
        return data[reg];
    }

    public void write(int reg, byte data) {
        this.data[reg] = data;
    }

}
