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

public abstract class BehavioralController extends Controller {

    protected boolean halt;

    protected abstract void onMachineCycle() throws Exception;
    protected abstract void onReset();
    
    public BehavioralController() {
    }
      
    @Override
    public void cycle() throws Exception {
        onMachineCycle();
    }
    
    public void reset() {
        onReset();
    }
    
    @Override
    public boolean isHalted() {
        return halt;
    }
    
    @Override
    public void run() throws Exception {
        reset();
        while (!halt) {
            cycle();
        }
    }
}
