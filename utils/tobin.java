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
package utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author skrbe
 */
public class tobin {
    public static void main(String[] args) {
        int group = 4;
        int addr = 0;
        LineNumberReader f = null;
        if (args.length < 3) {
            System.out.println("usage: tobin <memsize> <infile> <outfile>");
            return;
        }
        byte[] mem = new byte[Integer.parseInt(args[0])];
        try {
            String line;
            f = new LineNumberReader(new FileReader(args[1]));
            while ((line = f.readLine()) != null) {
                if (line.indexOf(';') >= 0) {
                    line = line.substring(0, line.indexOf(';'));
                }
                if (line.trim().equals("")) {
                    continue;
                }
                if (line.startsWith("@1")) {
                    group = 1;
                    continue;
                }
                if (line.startsWith("@2")) {
                    group = 2;
                    continue;
                }
                if (line.startsWith("@4")) {
                    group = 4;
                    continue;
                }
                if (line.startsWith("@!2")) {
                    group = -2;
                    continue;
                }
                if (line.startsWith("@!4")) {
                    group = -4;
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }
                int i = 0;
                String[] token = null;
                if (line.startsWith(":")) {
                    line = line.substring(1, line.length());
                    token = line.split("[ \t\n\r][ \t\n\r]*");
                    addr = Integer.parseInt(token[0],16);
                    i++;
                } else {
                    token = line.split("[ \t\n\r][ \t\n\r]*");                    
                }
                
                for(; i < token.length; i++) {
                    if (token[i].trim().equals("")) {
                        continue;
                    }
                    if (token[i].startsWith(";")) {
                        break;
                    }
                    switch (group) {
                        case 1: mem[addr++] = 
                                (byte)(Integer.parseInt(token[i],16)&0xFF);
                            break;
                        case 2: short vs = 
                                (short)(Integer.parseInt(token[i],16)&0xFFFF);
                            mem[addr++] = (byte)(vs & 0xFF);
                            mem[addr++] = (byte)((vs >> 8) & 0xFF);
                            break;
                        case 4: int vi = (int)(Long.parseLong(token[i],16) & 0xFFFFFFFFL);
                            mem[addr++] = (byte)(vi & 0xFF);
                            mem[addr++] = (byte)((vi >> 8) & 0xFF);
                            mem[addr++] = (byte)((vi >> 16) & 0xFF);
                            mem[addr++] = (byte)((vi >> 24) & 0xFF);
                            break;
                        case -2: vs = (short)(byte)(Integer.parseInt(token[i],16)&0xFFFF); 
                            mem[addr++] = (byte)((vs >> 8) & 0xFF);
                            mem[addr++] = (byte)(vs & 0xFF);
                            break;
                        case -4: vi = (int)(Long.parseLong(token[i],16) & 0xFFFFFFFFL);
                            mem[addr++] = (byte)((vi >> 24) & 0xFF);
                            mem[addr++] = (byte)((vi >> 16) & 0xFF);
                            mem[addr++] = (byte)((vi >> 8) & 0xFF);
                            mem[addr++] = (byte)(vi & 0xFF);
                            break;
                    }
                }
            }
            
            FileOutputStream o = new FileOutputStream(args[2]);
            o.write(mem);
            o.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(tobin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(tobin.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                f.close();
            } catch (IOException ex) {
                Logger.getLogger(tobin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
