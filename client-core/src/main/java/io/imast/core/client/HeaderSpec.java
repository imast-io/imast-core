package io.imast.core.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The header specification
 * 
 * @author davitp
 */
@AllArgsConstructor
public class HeaderSpec {
   
    /**
     * The header key
     */
    @Getter
    private final String header;
    
    /**
     * The value of header
     */
    @Getter
    private final String value;
}
