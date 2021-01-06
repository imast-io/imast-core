package io.imast.core.discovery;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The discovery instance definition
 * 
 * @author davitp
 */
@Getter
@AllArgsConstructor
public class DiscoveryInstance {
    
    /**
     * The host name
     */
    private final String host;
    
    /**
     * The port of instance
     */
    private final Integer port;
    
    /**
     * The base prefix
     */
    private final String base;
}

