package io.imast.core.scheduler.exchange;

import java.time.ZonedDateTime;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The status exchange request
 * 
 * @author davitp
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobStatusExchangeRequest {
    
    /**
     * The job group for status exchange
     */
    private String group;
    
    /**
     * The job type 
     */
    private String type;
    
    /**
     * The cluster
     */
    private String cluster;
    
    /**
     * The last known job state
     */
    private HashMap<String, ZonedDateTime> state;
}
