/*
 * *****************************************************************************
 *
 * Computer Architecture Courseware 1.0
 *
 * Miroslav Skrbek (C) 2013,2014
 *
 * Free to use, copy and modify for non-commercial, educational and research
 * purposes only.
 *
 * NO WARRANTY OF ANY KIND.
 *
 *****************************************************************************
 */
package components.regfiles;

public class RegFile32 {

    private final int[] data;

    public RegFile32(int size) {
        data = new int[size];
    }

    public int read(int reg) {
        return data[reg];
    }

    public void write(int reg, int data) {
        this.data[reg] = data;
    }

}
