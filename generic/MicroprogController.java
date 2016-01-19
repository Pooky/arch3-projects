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

import java.io.IOException;

/**
 *
 * @author skrbek
 */
public abstract class MicroprogController extends Controller {

    private int state = 0;
    private int tmp = 0;
    private Microcode microcode;
    private PSW psw;
    protected boolean halt;

    protected abstract void onLogic(Microinstruction m);

    protected abstract void onRisingClockEdge(Microinstruction m);

    protected abstract int onDecodeInstruction();

    protected void setState(int newState) {
        tmp = newState;
    }

    public MicroprogController(Microcode microcode, PSW psw) {
        this.microcode = microcode;
        this.psw = psw;
    }
  
    public Microinstruction getCurrentMicroinstruction() {
        return microcode.get(state);
    }
    
    public void step() {
        clock();
        while (state != 0) {
            clock();
        }
    }
    public void clock() {
        Microinstruction m = microcode.get(state);
        onLogic(m);
        onRisingClockEdge(m);
        switch (m.cond) {
            case NEXT:
                tmp = state + 1;
                break;
            case SKIP:
                tmp = m.targetState;
                break;
            case HALT:
                halt = true;
                tmp = state;
                break;
            case SKIP_BY_DECODER:
                tmp = onDecodeInstruction();
                break;
            case SKIP_IF_C:
                tmp = (psw.getFlag(PSW.Flags.FLAG_C)) ? m.targetState : state + 1;
                break;
            case SKIP_IF_NC:
                tmp = !(psw.getFlag(PSW.Flags.FLAG_C)) ? m.targetState : state + 1;
                break;
            case SKIP_IF_Z:
                tmp = (psw.getFlag(PSW.Flags.FLAG_Z)) ? m.targetState: state + 1;
                break;
            case SKIP_IF_NZ:
                tmp = !(psw.getFlag(PSW.Flags.FLAG_Z)) ? m.targetState : state + 1;
                break;
            case SKIP_IF_V:
                tmp = (psw.getFlag(PSW.Flags.FLAG_V)) ? m.targetState : state + 1;
                break;
            case SKIP_IF_NV:
                tmp = !(psw.getFlag(PSW.Flags.FLAG_V)) ? m.targetState : state + 1;
                break;
            case SKIP_IF_I:
                tmp = (psw.getFlag(PSW.Flags.FLAG_I)) ? m.targetState : state + 1;
                break;
            case SKIP_IF_NI:
                tmp = (psw.getFlag(PSW.Flags.FLAG_I)) ? m.targetState : state + 1;
                break;

        }
        state = tmp;
    }
    
    public void reset() {
        state = 0;
    }

    public void loadMicrocodeFromFile(String filename) throws IOException {
        microcode.loadFromFile(filename);
    }

    public int getState() {
        return state;
    }

    public boolean isHalted() {
        return halt;
    }
}
