/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 *
 * @author skrbek
 */
public abstract class HexFileLoader {
    
    public HexFileLoader() {        
    }
    
    protected abstract void writeByte(long addr, byte v) throws Exception;
    
    private long addrH = 0;
   
    public void parse(String line, int lineno) throws Exception {
        if (line == null || line.charAt(0) != ':') {
            return;
        }
        int sum;
        long addr = 0;
        int i = 1;
        int reclen = Integer.parseInt(line.substring(i, i+2), 16); i += 2;
        int recoffs = Integer.parseInt(line.substring(i, i+4), 16); i += 4;
        int rectype = Integer.parseInt(line.substring(i, i+2), 16); i += 2;
        sum = reclen;
        sum = sum + ((recoffs >> 8) & 0xFF);
        sum = sum + ((recoffs >> 0) & 0xFF);
        sum = sum + rectype;
        switch (rectype) {
            case 0x00:
                addr = recoffs + (addrH << 16); 
                for(int k = 0; k < reclen; k++) {
                    int v = Integer.parseInt(line.substring(i, i+2), 16);
                    sum = sum + v;
                    writeByte(addr + k, (byte) v);
                    i += 2;
                }
                sum = sum + Integer.parseInt(line.substring(i, i+2), 16);
                sum &= 0xFF;
                if (sum != 0) {
                    throw new Exception("invalid Hex file format");
                }
                break;
            case 0x04:
                if (reclen > 2 && recoffs != 0) {
                    throw new Exception("invalid Hex file format at line " + lineno);                    
                }
                addrH = 0;
                for(int k = 0; k < reclen; k++) {
                    int v = Integer.parseInt(line.substring(i, i+2), 16);
                    sum = sum + v;
                    addrH = (addrH << 8) + (v & 0xFF); 
                    i += 2;
                }
                sum = sum + Integer.parseInt(line.substring(i, i+2), 16);
                sum &= 0xFF;
                if (sum != 0) {
                    throw new Exception("invalid Hex file format");
                }
        }
    }
    
    public void loadHexFile(String filename) throws FileNotFoundException, IOException, Exception {
        String line;
        LineNumberReader reader;
        reader = new LineNumberReader(new FileReader(filename));
        do {  
            line = reader.readLine();
     //       System.out.println(line);
            parse(line, reader.getLineNumber());
            
        } while (line != null);
    }
} 
