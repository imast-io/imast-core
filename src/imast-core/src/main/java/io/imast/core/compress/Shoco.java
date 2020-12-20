package io.imast.core.compress;

import io.imast.core.ext.adt.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * The Shoco compression Refer to GitHub/Quantum64/Jstx
 *
 * @author davitp
 */
public class Shoco {
    
    /**
     * Compress a given string into a byte array
     * 
     * @param str The given string
     * @return Returns compressed output
     */
    public static byte[] compress(String str) {
        
        // no string
        if(str == null){
            return new byte[0];
        }
        
        // string as chars
        var chars = str.getBytes(StandardCharsets.UTF_8);
        
        // buffer data
        var data = new ByteBuffer(chars.length * 2);
        
        
        for (int index = 0; index < chars.length; index++) {
            byte in = chars[index];
            short[] indices = new short[8];
            indices[0] = ShocoConstants.FIRST_CHARS[in & 0xff];
            int lastIndex = indices[0];
            if (lastIndex >= 0) {
                int consecutive = 1;
                for (; consecutive <= 7; ++consecutive) {
                    if (index + consecutive >= chars.length) {
                        break;
                    }
                    int currentIndex = ShocoConstants.FIRST_CHARS[chars[index + consecutive] & 0xff];
                    if (currentIndex < 0) {
                        break;
                    }
                    int successorIndex = ShocoConstants.SUCCESSOR_IDS[lastIndex][currentIndex];
                    if (successorIndex < 0) {
                        break;
                    }
                    indices[consecutive] = (short) successorIndex;
                    lastIndex = currentIndex;
                }
                if (consecutive > 1) {
                    int pack = -1;
                    for (int p = ShocoConstants.PACKS.length - 1; p >= 0; --p) {
                        boolean indice = true;
                        for (int i = 0; i < ShocoConstants.PACKS[p].unpacked; ++i) {
                            if (indices[i] > ShocoConstants.PACKS[p].masks[i]) {
                                indice = false;
                                break;
                            }
                        }
                        if ((consecutive >= ShocoConstants.PACKS[p].unpacked) && indice) {
                            pack = p;
                            break;
                        }
                    }
                    if (pack >= 0) {
                        long word = ShocoConstants.PACKS[pack].word;
                        for (int i = 0; i < ShocoConstants.PACKS[pack].unpacked; ++i) {
                            word |= indices[i] << ShocoConstants.PACKS[pack].offsets[i];
                        }

                        byte[] packed = new byte[8];
                        for (int i = 7; i >= 0; i--) {
                            packed[i] = (byte) (word & 0xFF);
                            word >>= 8;
                        }

                        for (int i = 0; i < ShocoConstants.PACKS[pack].packed; ++i) {
                            data.put(packed[4 + i]);
                        }

                        index += ShocoConstants.PACKS[pack].unpacked - 1;
                        continue;
                    }
                }
            }
            if ((in & 0x80) != 0) {
                data.put((byte) 0x00);
            }
            data.put(in);
        }
        
        // the result stream
        var result = new byte[data.pointer()];
        
        // copy array form buffer
        System.arraycopy(data.array(), 0, result, 0, result.length);
        return result;
    }

    /**
     * Decompress the given bytes into a string
     * 
     * @param chars The chars to decompress
     * @return Returns decompressed string
     */
    public static String decompress(byte[] chars) {
        ByteBuffer out = new ByteBuffer(chars.length * 2);
        for (int index = 0; index < chars.length; index++) {
            byte in = chars[index];
            int mark = -1;
            byte val = chars[index];
            while (val < 0) {
                val <<= 1;
                ++mark;
            }
            if (mark < 0) {
                if (in == 0x00) {
                    index++;
                    out.put(chars[index]);
                    continue;
                }
                out.put(in);
            } else {
                byte[] packed = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
                for (int i = 0; i < ShocoConstants.PACKS[mark].packed; i++) {
                    packed[4 + i] = chars[index + i];
                }
                long word = 0;
                for (int i = 0; i < 8; i++) {
                    word <<= 8;
                    word |= (packed[i] & 0xFF);
                }
                int offset = ShocoConstants.PACKS[mark].offsets[0];
                int mask = ShocoConstants.PACKS[mark].masks[0];
                byte lastChar = (byte) ShocoConstants.FIRST_IDS[(int) ((word >> offset) & mask)];
                out.put(lastChar);
                for (int i = 1; i < ShocoConstants.PACKS[mark].unpacked; ++i) {
                    offset = ShocoConstants.PACKS[mark].offsets[i];
                    mask = ShocoConstants.PACKS[mark].masks[i];
                    lastChar = (byte) ShocoConstants.SUCCESSOR_CHARS[(lastChar & 0xff) - 39][(int) ((word >> offset) & mask)];
                    out.put(lastChar);
                }
                index += ShocoConstants.PACKS[mark].packed - 1;
            }
        }
        byte[] data = new byte[out.pointer()];
        System.arraycopy(out.array(), 0, data, 0, data.length);
        return new String(data, StandardCharsets.UTF_8);
    }
}
