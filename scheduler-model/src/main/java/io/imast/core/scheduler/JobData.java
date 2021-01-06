package io.imast.core.scheduler;

import java.io.Serializable;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The data for the job execution
 * 
 * @author davitp
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobData implements Serializable {
    
    /**
     * The actual data in key value pair format
     */
    private HashMap<String, Object> data;   
}
