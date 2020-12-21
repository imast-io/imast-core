package io.imast.core.adt;

/**
 * The byte buffer in place
 * 
 * @author davitp
 */
public class ByteBuffer {
    
    /**
     * The internal storage
     */
    private final byte[] internal;
    
    /**
     * The current pointer
     */
    private int pointer;

    /**
     * Creates new instance of buffer
     * 
     * @param size The buffer size
     */
    public ByteBuffer(int size) {
        this.internal = new byte[size];
        this.pointer = 0;
    }

    /**
     * Gets the pointer to byte
     * 
     * @return Returns the pointer
     */
    public int pointer() {
        return this.pointer;
    }
     
    /**
     * Put a byte into the buffer
     * 
     * @param item The byte to add
     */
    public void put(byte item) {
        this.internal[this.pointer] = item;
        this.pointer++;
    }

    /**
     * The array form 
     * 
     * @return Returns internal array
     */
    public byte[] array() {
        return this.internal;
    }   
}
