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

public abstract class Controller {
    public abstract void run() throws Exception;
    public abstract void cycle() throws Exception;
    public abstract void clock() throws Exception;
    public abstract void stop();
    public abstract boolean isHalted();
}
