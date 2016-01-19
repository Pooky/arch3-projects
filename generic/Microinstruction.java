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

public class Microinstruction {

    public enum Condition {

        NEXT("Next"),
        SKIP("Skip"),
        SKIP_IF_Z("Skip-if-zero"),
        SKIP_IF_NZ("Skip-if-non-zero"),
        SKIP_IF_C("Skip-if-carry"),
        SKIP_IF_NC("Skip-if-non-carry"),
        SKIP_IF_V("Skip-if-overflow"),
        SKIP_IF_NV("Skip-if-non-overflow"),
        SKIP_IF_N("Skip-if-negative"),
        SKIP_IF_P("Skip-if-pozitive"),
        SKIP_IF_I("Skip-if-interrupt"),
        SKIP_IF_NI("Skip-if-non-interrupt"),
        SKIP_BY_DECODER("Skip-by-decoder"),
        HALT("Halt");
        final String name;

        Condition(String name) {
            this.name = name;
        }
    }
    public int targetState = 0;
    public Condition cond = Condition.NEXT;
    
    public Microinstruction() {                                                                 
    }

    public Microinstruction(int next, Condition cond) {
        this.targetState = next;
        this.cond = cond;
    }
    
    public int parse(String line) {      
        return 0;
    }
    
}
