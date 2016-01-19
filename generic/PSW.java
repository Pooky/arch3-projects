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

public interface PSW {
    public enum Flags {
        FLAG_C, FLAG_Z, FLAG_N, FLAG_V, FLAG_I, FLAG_HC
    };
    public void clear();
    public void setFlag(Flags f, boolean v);
    public boolean getFlag(Flags f);
    public abstract long getFlags(); 
    public abstract void setFlags(long flags); 
    public abstract String getFlagsAsString();
}
