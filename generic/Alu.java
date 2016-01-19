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

public class Alu {

    public static enum AluOp {

        NOP("NOP"), // no operation, zero output
        ADD("ADD"), // add two operands
        ADDC("ADDC"), // add two operands with carry
        SUB("SUB"), // substract two operands 
        SUBB("SUBB"), // substract two operands with borrow
        SUBR("SUBR"), // substract two operands 
        SUBBR("SUBBR"), // substract two operands with borrow
        CPL("CPL"), // bitwise not
        NEG("NEG"), // two's complement (unary minus)
        AND("AND"), // bitwise and
        OR("OR"), // bitwise or
        XOR("XOR"), // bitwise xor
        SRC1("SRC1"), // SRC1
        SRC2("SRC2"), // SRC2
        SHL("SHL"), // shift left
        SHR("SHR"), // shift right
        ASR("ASR"), // arithmetic shift right
        ROL("ROL"), // rotate left 
        ROR("ROR"), // rotate right
        RCL("RCL"), // rotate left with carry
        RCR("RCR"), // rotate right with carry
        ZEB("ZEB"), // zero extended byte
        SEB("SEB"); // sign extended byte
        
        private final String OpCodeName;

        private AluOp(String name) {
            OpCodeName = name;
        }
    }

    
    // todo: kontrole subr, subbr, sub, subb
    
    public static byte alu8(AluOp op, byte src1, byte src2, PSW psw) {
        return (byte) exec(8, op, src1, src2, psw);
    }

    public static short alu16(AluOp op, short src1, short src2, PSW psw) {
        return (short) exec(16, op, src1, src2, psw);
    }

    public static int alu32(AluOp op, int src1, int src2, int dest, PSW psw) {
        return (int) exec(32, op, src1, src2, psw);
    }

    public static long alu64(AluOp op, long src1, long src2, long dest, PSW psw) {
        return exec(64, op, src1, src2, psw);
    }

    public static long exec(int bits, AluOp op, long src1, long src2, PSW psw) {
        long f;
        long r = -1;
        long m0 = ~((long) -1 << (bits));
        long m1 = (long) 1 << (bits - 1);
        long m2 = (long) 1 << (bits - 2);
        long m3 = (long) 1 << 4;
        switch (op) {
            case ADD: // ADD
                r = (src1 + src2);
                f = ((src1 & src2) | (src1 & ~r)
                        | (src2 & ~r));
                psw.setFlag(PSW.Flags.FLAG_C, (f & m1) != 0);
                psw.setFlag(PSW.Flags.FLAG_V, ((f & m1) != 0) != ((f & m2) != 0));
                psw.setFlag(PSW.Flags.FLAG_HC, (f & m3) != 0);
                break;
            case ADDC: { // ADDC
                long c = (psw.getFlag(PSW.Flags.FLAG_C)) ? 1 : 0;
                r = (src1 + src2 + c);
                f = ((src1 & src2) | (src1 & ~r)
                        | (src2 & ~r));
                psw.setFlag(PSW.Flags.FLAG_C, (f & m1) != 0);
                psw.setFlag(PSW.Flags.FLAG_V, ((f & m1) != 0) != ((f & m2) != 0));
                psw.setFlag(PSW.Flags.FLAG_HC, (f & m3) != 0);
            }
            break;
            case SUB: // SUB
                r = (src1 - src2);
                f = ((src1 & src2) | (src1 & ~r)
                        | (src2 & ~r));
                psw.setFlag(PSW.Flags.FLAG_C, (f & m1) != 0);
                psw.setFlag(PSW.Flags.FLAG_V, ((f & m1) != 0) != ((f & m2) != 0));
                psw.setFlag(PSW.Flags.FLAG_HC, (f & m3) != 0);
                break;
            case SUBB: { // SUBB
                long c = (psw.getFlag(PSW.Flags.FLAG_C)) ? 0 : 1;
                r = (short) (src2 - src1 + c - 1);
                f = (short) ((src1 & src2) | (src1 & ~r)
                        | (src2 & ~r));
                psw.setFlag(PSW.Flags.FLAG_C, (f & m1) != 0);
                psw.setFlag(PSW.Flags.FLAG_V, ((f & m1) != 0) != ((f & m2) != 0));
                psw.setFlag(PSW.Flags.FLAG_HC, (f & m3) != 0);
            }
            case SUBR: // SUB
                r = (src2 - src1);
                f = ((src2 & src1) | (src2 & ~r)
                        | (src1 & ~r));
                psw.setFlag(PSW.Flags.FLAG_C, (f & m1) != 0);
                psw.setFlag(PSW.Flags.FLAG_V, ((f & m1) != 0) != ((f & m2) != 0));
                psw.setFlag(PSW.Flags.FLAG_HC, (f & m3) != 0);
                break;
            case SUBBR: { // SUBBR
                long c = (psw.getFlag(PSW.Flags.FLAG_C)) ? 0 : 1;
                r = (short) (src2 - src1 + c - 1);
                f = (short) ((src1 & src2) | (src1 & ~r)
                        | (src2 & ~r));
                psw.setFlag(PSW.Flags.FLAG_C, (f & m1) != 0);
                psw.setFlag(PSW.Flags.FLAG_V, ((f & m1) != 0) != ((f & m2) != 0));
                psw.setFlag(PSW.Flags.FLAG_HC, (f & m3) != 0);
            }
            break;
            case CPL: // CPL
                r = (short) ~src1;
                break;
            case NEG: // NEG
                r = (short) (~src1 + 1);
                break;

            case AND: // AND
                r = (short) (src1 & src2);
                break;
            case OR: // OR
                r = (short) (src1 | src2);
                break;
            case XOR: // XOR
                r = (short) (src1 ^ src2);
                break;
            case SRC1: // SRC1
                r = src1;
                break;
            case SRC2: // SRC2
                r = src2;
                break;
            case SHL: // SHL
                src2 %= bits;
                if (src2 > 0) {
                    psw.setFlag(PSW.Flags.FLAG_C,
                            (src1 & (m1 >> (bits - src2))) != 0);
                    r = (src1 << src2) & m0;
                } else {
                    r = src1;
                }
                break;
            case SHR: // SHR
                src2 %= bits;
                if (src2 > 0) {
                    psw.setFlag(PSW.Flags.FLAG_C, (src1 & (1 << (src2 - 1))) != 0);
                    r = (src1 & m0) >>> src2;
                } else {
                    r = src1;
                }
                break;
            case ASR: { // ASR
                src2 %= bits;
                if (src2 > 0) {
                    psw.setFlag(PSW.Flags.FLAG_C, (src1 & (1 << src2)) != 0);
                    int bsh = 64 - bits;
                    r = ((src1 << bsh) >> (src2 + bsh)) & m0;
                } else {
                    r = src1;
                }
            }
            break;
            case ROL: { // ROL
                //r = (src1 << src2) | ((src1 >> (bits - src2)) & ~(m0 << bits));
                boolean cf = (src1 & m1) != 0;
                psw.setFlag(PSW.Flags.FLAG_C, cf);
                r = (src1 << 1);
                r |= (cf) ? 1 : 0;
            }
            break;
            case ROR: {// ROR
                boolean cf = (src1 & 1) != 0;
                psw.setFlag(PSW.Flags.FLAG_C, cf);
                r = (src1 >> 1);
                r = (r & ~m1) | ((cf) ? m1 : 0);
            }
            break;

            case RCL: {// RCx
                boolean cf = (src1 & m1) != 0;
                r = (src1 << 1);
                r |= (psw.getFlag(PSW.Flags.FLAG_C)) ? 1 : 0;
                psw.setFlag(PSW.Flags.FLAG_C, cf);
            }
            break;
            case RCR: {// RCx
                boolean cf = (src1 & 1) != 0;
                r = (src1 >> 1);
                r |= (r & ~m1) | ((psw.getFlag(PSW.Flags.FLAG_C)) ? m1 : 0);
                psw.setFlag(PSW.Flags.FLAG_C, cf);
            }
            break;
            case ZEB:
                r = src1 & 0xFF;
                break;
            case SEB:
                r = (src1) | (((src1 & 0x80) != 0) ? -1 : 0);
                break;
        }
        psw.setFlag(PSW.Flags.FLAG_N, (r & m1) != 0);
        psw.setFlag(PSW.Flags.FLAG_Z, (r & m0) == 0);
        return r & m0;
    }
}
