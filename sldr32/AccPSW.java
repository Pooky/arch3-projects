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
package sldr32;

import components.registers.Reg16;
import generic.PSW;
import generic.Reg;

public class AccPSW extends Reg implements PSW {

	// singleton
	private static AccPSW instance = null;
	
    /* PSW FLAG CONSTANTS */
    public static final int FLAG_Z = 0x0000001;
    public static final int FLAG_C = 0x0000002;
    public static final int FLAG_V = 0x0000004;
    public static final int FLAG_N = 0x0000008;
    public static final int FLAG_I = 0x8000000;
    public static final int FLAG_ALL = 0x0000000F;

    private short flagsIn;
    private short flagsOut;

    public static AccPSW getInstance() {
    	
        if(instance == null) {
           instance = new AccPSW();
        }
        return instance;
        
     }
    
    @Override
    public void clear() {
        flagsIn = 0;
    }

    @Override
    public void setFlag(Flags f, boolean v) {
        switch (f) {
            case FLAG_C:  flagsIn = (short) ((flagsIn & ~AccPSW.FLAG_C) |
                    ((v) ? AccPSW.FLAG_C: 0)); break;
            case FLAG_Z:  flagsIn = (short) ((flagsIn & ~AccPSW.FLAG_Z) | 
                    ((v) ? AccPSW.FLAG_Z: 0)); break;  
            case FLAG_V:  flagsIn = (short) ((flagsIn & ~AccPSW.FLAG_V) | 
                    ((v) ? AccPSW.FLAG_V: 0)); break;  
            case FLAG_N:  flagsIn = (short) ((flagsIn & ~AccPSW.FLAG_N) | 
                    ((v) ? AccPSW.FLAG_N: 0)); break;  
            case FLAG_I:  flagsIn = (short) ((flagsIn & ~AccPSW.FLAG_I) | 
                    ((v) ? AccPSW.FLAG_I: 0)); break;  
        }
    }

    @Override
    public boolean getFlag(Flags f) {
        switch (f) {
            case FLAG_C:  return (flagsOut & AccPSW.FLAG_C) != 0;
            case FLAG_Z:  return (flagsOut & AccPSW.FLAG_Z) != 0;
            case FLAG_V:  return (flagsOut & AccPSW.FLAG_V) != 0;
            case FLAG_N:  return (flagsOut & AccPSW.FLAG_N) != 0;
            case FLAG_I:  return (flagsOut & AccPSW.FLAG_I) != 0;
        }
        return false;
    }    

    @Override
    public long getFlags() {
        return flagsOut & 0xFFFF;
    }
    
    @Override
    public void setFlags(long flags) {
        flagsIn = (short) flags;
    }

    public String getFlagsAsString() {
        short f = flagsOut;
        String s = "";
        if ((f & FLAG_C) != 0) s="C"+s; else s="-"+s;
        if ((f & FLAG_Z) != 0) s="Z"+s; else s="-"+s;
        if ((f & FLAG_V) != 0) s="V"+s; else s="-"+s;
        if ((f & FLAG_N) != 0) s="N"+s; else s="-"+s;
        if ((f & FLAG_I) != 0) s="I"+s; else s="-"+s;
        return s;
    }
    
    @Override
    public void clock() {
        flagsOut = flagsIn;
    }

    public short output() {
        return (short)getFlags();
    }


}
