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
package str_arch;

import generic.Microcode;
import generic.Microinstruction;

public class AccMicrocode extends Microcode {

    AccMicrocode(String[] mc) {
        super(mc);
    }

    @Override
    protected Microinstruction create() {
        return new AccMicroinstr();
    }
}
