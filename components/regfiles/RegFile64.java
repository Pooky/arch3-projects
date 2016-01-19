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

public class RegFile64 {

    private final long[] data;

    public RegFile64(int size) {
        data = new long[size];
    }

    public long read(int reg) {
        return data[reg];
    }

    public void write(int reg, long data) {
        this.data[reg] = data;
    }

}
