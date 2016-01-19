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
package sldr16;

import components.alus.Alu16;
import components.busses.Bus16x16;
import components.registers.Reg16;
import generic.SystemBus;
import generic.MicroprogController;
import generic.Core;
import generic.Microinstruction;

public class AccCore extends Core {

    /* Registers */
    private Reg16 PC = new Reg16();
    private Reg16 A = new Reg16();
    private Reg16 IR = new Reg16();
    private AccPSW psw = new AccPSW();

    /* Data paths */
    private short ab;
    private short db;
    private short aluo;

    /* Busses */
    Bus16x16 systemBus;

    /* Microcode */
    String[] mc = new String[]{
        // state  cond skip-to-state aluop src1s src2s      k moe mwr doe aoe asel aw pcw irw psww pswsel
        // IF
        "  0      NEXT             0   NOP     0     0      0   0   0   0   1   1   0   0   0    0      0",
        "  1      NEXT             0   ADD     2     1      2   1   0   0   1   1   0   1   1    0      0",
        // ID-0
        "  2      SKIP_BY_DECODER  0   NOP     2     1      2   0   0   0   1   1   0   0   0    0      0",
        //// NOP //////////////////////////////////////////////////////////////////////////////////////////
        //state cond skip-to-state aluop src1s src2s      k moe mwr doe aoe asel aw pcw irw psww pswsel
        // ID-1
        "  3    SKIP               0   NOP     2     1    2   0   0   0   0   0   0   1   1    0      0",
        //// JMP <addr> ///////////////////////////////////////////////////////////////////////////////////
        //state cond skip-to-state aluop src1s src2s      k moe mwr doe aoe asel aw pcw irw psww pswsel
        // ID-1
        " 10      NEXT             0   ADD     2     1      2   1   0   0   1   1   0    1   1   0      0",
        // EX+WB  
        " 11      NEXT             0   SRC1    0     0     0    0   0   0   0   0    0   0   0   0      0",
        " 12      SKIP             0   SRC1    0     0     0    0   0   0   0   0    0   1   0   0      0",
        //// LDA <adr> ///////////////////////////////////////////////////////////////////////////////
        //state cond skip-to-state aluop src1s src2s      k moe mwr doe aoe asel aw pcw irw psww pswsel
        // ID-1
        " 15      NEXT             0   ADD     2     1      2   1   0   0   1    1  0   1   1    0     0",
        // OF  
        " 16      NEXT             0   SRC1    1     0      0   0   0   0   1    0   0  0   0    0      0",
        " 17      NEXT             0   SRC1    1     0      0   1   0   0   1    0   0  0   1    0      0",
        // EX+WB  
        " 18      NEXT             0   SRC1    0     0      0   0   0   0   0   0    0  0   0   0       0",
        " 19      SKIP             0   SRC1    0     0      0   0   0   0   0   0    1  0   0   0       0",
        //// ADD <addr> //////////////////////////////////////////////////////////////////////////////  
        //state cond skip-to-state aluop src1s src2s      k moe mwr doe aoe asel aw pcw irw psww pswsel
        // ID-1
        " 20     NEXT             0   ADD     2     1      2   1   0   0   1   1   0   1   1    0      0",
        // OF  
        " 21     NEXT             0   SRC1    1     0      0   0   0   0   1   0   0   0   0    0      0",
        " 22     NEXT             0   SRC1    1     0      0   1   0   0   1   0   0   0   1    0      0",
        // EX+WB  
        " 23     NEXT             0   ADD     0     2      0   0   0   0   0   0   0   0   0    0      0",
        " 24     SKIP             0   ADD     0     2      0   0   0   0   0   0   1   0   0    1      0",
        //// CALL <adr> //////////////////////////////////////////////////////////////////////////////
        // state cond skip-to-state aluop src1s src2s      k moe mwr doe aoe asel aw pcw irw psww pswsel
        // ID-1
        " 25     NEXT             0   NOP     0     0      0   0   0   0   0   0   0   0   0    0      0",
        // SP -> IR   
        " 26     NEXT             0   NOP     0     0      0   0   0   0   1   3   0   0   0    0      0",
        " 27     NEXT             0   NOP     0     0      0   1   0   0   1   3   0   0   1    0      0",
        // IR-2 -> SP
        " 28     NEXT             0   SUB     2     0      2   0   0   1   1   3   0   0   0    0      0",
        " 29     NEXT             0   SUB     2     0      2   0   1   1   1   3   0   0   0    0      0",
        // PC+2 -> [IR]
        " 30     NEXT             0   ADD     2     1      2   0   0   0   1   0   0   0   0    0      0",
        " 31     NEXT             0   ADD     2     1      2   0   1   1   1   0   0   0   0    0      0",
        // [PC] -> PC (target address) 
        " 32     NEXT             0   SRC1    1     0      0   0   0   0   1   1   0   0   0    0      0",
        " 33     SKIP             0   SRC1    1     0      0   1   0   0   1   1    0  1   0    0      0",
        //// RET /////////////////////////////////////////////////////////////////////////////////////////
        //state cond skip-to-state aluop src1s src2s      k moe mwr doe aoe asel aw pcw irw psww pswsel
        // ID-1
        " 35     NEXT             0   NOP     0     0      0   0   0   0   0   0   0   0   0    0      0",
        // SP -> IR  
        " 36     NEXT             0   NOP     0     0      0   0   0   0   1   3   0   0   0    0      0",
        " 37     NEXT             0   NOP     0     0      0   1   0   0   1   3   0   0   1    0      0",
        //  IR+2 -> SP, IR+2 -> PC
        " 38     NEXT             0   ADD     2     0      2   0   0   1   1   3   0   0   0    0      0",
        " 39     NEXT             0   ADD     2     0      2   0   1   1   1   3   0   1   0    0      0",
        //  [PC]  -> PC
        " 40     NEXT             0  SRC1     1     0      0   0   0   0   1   1   0   0   0    0      0",
        " 41     SKIP             0  SRC1     1     0      0   1   0   0   1   1   0   1   0    0      0"
    //////////////////////////////////////////////////////////////////////////////////////////////////
    };
    private final AccMicrocode microcode = new AccMicrocode(mc);

    /* Functional Blocks */
    private final Alu16 alu = new Alu16();
    private final MicroprogController controller = new MicroprogController(microcode, psw) {

        @Override
        protected int onDecodeInstruction() {
            switch (((int) IR.getQ() & 0xFFFF)) {
                // NOP
                case 0x0000:
                    return 3;
                // JMP
                case 0x4000:
                    return 10;
                // LDA        
                case 0x8000:
                    return 15;
                // ADD        
                case 0x0200:
                    return 20;
                // CALL        
                case 0xc000:
                    return 25;
                // RET
                case 0xc001:
                    return 35;
                default:
                    java.lang.System.out.printf("Error: unknown instruction code [%04x]\n", (int) IR.getQ());
                    java.lang.System.exit(1);
            }
            return 0;
        }

        @Override
        protected void onLogic(Microinstruction mi) {
            AccMicroinstr m = (AccMicroinstr) mi;
            short op1 = 0, op2 = 0;

            ab = (short) ((!m.aoe) ? 0xFFFF : (m.asel == 0) ? IR.getQ()
                    : (m.asel == 1) ? PC.getQ() : 0x10);

            if (m.moe) {
                if (!m.aoe) {
                    java.lang.System.out.printf("Error: address bus in Z state\n");
                    java.lang.System.exit(1);
                }
                if (m.doe) {
                    java.lang.System.out.printf(
                            "Error: bus conflict on DB (moe and doe active simultaneously)\n");
                    java.lang.System.exit(1);
                }

                try {
                    db = systemBus.read(ab, SystemBus.M_16);
                } catch (Exception ex) {
                  System.out.println(ex.getMessage());
                  System.exit(1);
                }
            }

            switch (m.src1s) {
                case 0:
                    op1 = IR.getQ();
                    break;
                case 1:
                    op1 = db;
                    break;
                case 2:
                    op1 = (short) m.k;
                    break;
            }

            switch (m.src2s) {
                case 0:
                    op2 = IR.getQ();
                    break;
                case 1:
                    op2 = PC.getQ();
                    break;
                case 2:
                    op2 = A.getQ();
                    break;
                case 3:
                    op2 = psw.output();
                    break;
            }
            aluo = alu.exec(m.aluop, op1, op2, aluo, psw);

            if (m.asel == 2 && m.aoe) {
                ab = aluo;  
                
            }

            if (m.doe) {
                db = aluo;
            } else if (!m.moe) {
                db = (short) 0xFFFF;
            }

            // BUSes -> Registers
            PC.setD(aluo);
            IR.setD(db);
            A.setD(aluo);
            
            if (controller.getState() == 0) {
                print(true);
            }
            print(false);
        }

        @Override
        protected void onRisingClockEdge(Microinstruction m) {
            AccMicroinstr mi = (AccMicroinstr) getCurrentMicroinstruction();
            if (mi.pcw) {
                PC.clock();
            }
            if (mi.irw) {
                IR.clock();
            }
            if (mi.psww) {
                psw.clock();
            }
            if (mi.aw) {
                A.clock();
            }
            if (mi.mwr) {
                try {
                    systemBus.write(ab, db, SystemBus.M_16);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    System.exit(1);
                }
            }
        }

        @Override
        public void run() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void cycle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void stop() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    };

    AccCore(Bus16x16 systemBus) {
        this.systemBus = systemBus;
    }

    AccCore() {
    }

    /**
     * SLDACC_PRINT This function prints all registers and control isgnals of
     * the processor.
     */
    void print(boolean header) {
        AccMicroinstr m = (AccMicroinstr) controller.getCurrentMicroinstruction();

        if (header) {
            System.out.printf("# ");
        } else {
            System.out.printf("  ");
        }
        if (header) {
            System.out.printf("clk     ");
        } else {
            System.out.printf("%s  ", "^^\\__/");
        }
        if (header) {
            System.out.printf("state   ");
        } else {
            System.out.printf("[%4d]  ", controller.getState());
        }
        if (header) {
            System.out.printf("PC    ");
        } else {
            System.out.printf("%04X  ", PC.getQ());
        }
        if (header) {
            System.out.printf("A     ");
        } else {
            System.out.printf("%04X  ", A.getQ());
        }
        if (header) {
            System.out.printf("IR    ");
        } else {
            System.out.printf("%04X  ", IR.getQ());
        }
        if (header) {
            System.out.printf("PSW   ");
        } else {
            System.out.printf("%04X  ", psw.output());
        }
        if (header) {
            System.out.printf("PSW    ");
        } else {
            System.out.printf("%5s  ", psw.getFlagsAsString());
        }
        if (header) {
            System.out.printf("AB    ");
        } else {
            System.out.printf("%04X  ", ab);
        }
        if (header) {
            System.out.printf("DB    ");
        } else {
            System.out.printf("%04X  ", db);
        }
        if (header) {
            System.out.printf("ALUO  ");
        } else {
            System.out.printf("%04X  ", aluo);
        }
        if (header) {
            System.out.printf("aluop  ");
        } else {
            System.out.printf("%-5s  ", m.aluop.toString());
        }
        if (header) {
            System.out.printf("src1s ");
        } else {
            System.out.printf("%1d     ", m.src1s);
        }
        if (header) {
            System.out.printf("src2s ");
        } else {
            System.out.printf("%1d     ", m.src2s);
        }
        if (header) {
            System.out.printf("k     ");
        } else {
            System.out.printf("%04X  ", m.k);
        }

        if (header) {
            System.out.printf("moe ");
        } else {
            System.out.printf("%d   ", (m.moe) ? 1 : 0);
        }
        if (header) {
            System.out.printf("mwr ");
        } else {
            System.out.printf("%d   ", (m.mwr) ? 1 : 0);
        }
        if (header) {
            System.out.printf("doe ");
        } else {
            System.out.printf("%d   ", (m.doe) ? 1 : 0);
        }
        if (header) {
            System.out.printf("aoe ");
        } else {
            System.out.printf("%d   ", (m.aoe) ? 1 : 0);
        }
        if (header) {
            System.out.printf("asel ");
        } else {
            System.out.printf("%d    ", m.asel);
        }
        if (header) {
            System.out.printf("aw ");
        } else {
            System.out.printf("%d  ", (m.aw) ? 1 : 0);
        }

        if (header) {
            System.out.printf("pcw    ");
        } else {
            System.out.printf("%d      ", (m.pcw) ? 1 : 0);
        }
        if (header) {
            System.out.printf("irw    ");
        } else {
            System.out.printf("%d      ", (m.irw) ? 1 : 0);
        }
        if (header) {
            System.out.printf("psww   ");
        } else {
            System.out.printf("%d      ", (m.psww) ? 1 : 0);
        }
        if (header) {
            System.out.printf("pswsel ");
        } else {
            System.out.printf("%d      ", m.pswsel);
        }
        System.out.println();
    }

    public MicroprogController getController() {
        return controller;
    }

    @Override
    public void clock() {
        controller.clock();
    }

    @Override
    public void cycle() {
        controller.step();
    }

    @Override
    public boolean isHalted() {
        return controller.isHalted();
    }

    @Override
    public void run() {
        while (!isHalted()) {
            clock();
        }
    }

}
