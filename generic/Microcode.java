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
package generic;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Microcode extends ArrayList<Microinstruction> {
    
    public Microcode(Microinstruction[] mcode) {
        this.addAll(Arrays.asList(mcode));
    }
    
    public Microcode(String[] mcode) {
        loadFromStrings(mcode);
    }
    
    public Microcode() {
    }
    
    protected abstract Microinstruction create();
    
    public void loadFromStrings(String[] mcode) {
        for (String s : mcode) {
            Microinstruction m = create();
            int state = m.parse(s);
            while (this.size() <= state) {
                this.add(null);
            }
            this.set(state, m);
        }
    }
    
    public void loadFromArray(Microinstruction[] mcode) {
        this.addAll(Arrays.asList(mcode));
    }
    
    public void loadFromFile(String filename) throws FileNotFoundException, IOException {
        int idx = 0;
        LineNumberReader f = null;
        try {
            String line;
            f = new LineNumberReader(new FileReader(filename));
            while ((line = f.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }
                if (line.charAt(0) == '#' && line.charAt(1) == '#') {
                    continue;
                }
                if (line.charAt(0) == '#' || line.charAt(0) == '\n'
                        || line.charAt(0) == '\0') {
                    continue;
                }
                Microinstruction m = create();
                int state = m.parse(line);
                while (this.size() <= state) {
                    this.add(null);
                }
                this.set(state, m);
            }
        } finally {
            if (f != null) {
                f.close();
            }
        }
    }
    
}
