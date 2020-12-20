package io.imast.core.compress;

import lombok.AllArgsConstructor;

/**
 * The Shoco algorithm pack
 * 
 * @author davitp
 */
@AllArgsConstructor
public class ShocoPack {
    
    /**
     * The word
     */
    public final long word;
    
    /**
     * The packed integer
     */
    public final int packed;
    
    /**
     * The unpacked integer
     */
    public final int unpacked;
    
    /**
     * The offsets
     */
    public final int[] offsets;
    
    /**
     * The masks
     */
    public final short[] masks;
}
