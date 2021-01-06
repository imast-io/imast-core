package io.imast.core.discovery;

import io.imast.core.Str;
import java.util.HashMap;
import java.util.Map;

/**
 * A discovery client based on static metadata
 * 
 * @author davitp
 */
public class StaticDiscoveryClient implements DiscoveryClient {
    
    /**
     * The static environment
     */
    private final String environment;

    /**
     * The static ports 
     */
    private final HashMap<String, Integer> ports;
    
    /**
     * Creates new static discovery client
     * 
     * @param environment The environment address
     * @param mapping A mapping of service ports
     */
    public StaticDiscoveryClient(String environment, Map<String, Integer> mapping){
        this.environment = environment;
        this.ports = mapping == null ? new HashMap<>() : new HashMap<>(mapping);
    }
    
    /**
     * Gets next base URL from eureka if available
     * 
     * @param service The service to resolve
     * @return Returns eureka-resolved URL
     */
    @Override
    public String getNextBaseUrl(String service) {
        
        // get instance
        var instance = this.getInstance(service);
        
        // format the url
        return String.format("http://%s:%s/", instance.getHost(), instance.getPort());
    }
    
    /**
     * Gets the API URL using eureka
     * 
     * @param service The service 
     * @param api The API
     * @return Returns API URL
     */
    @Override
    public String getApiUrl(String service, String api){
        
        // get base URL
        String baseUrl = this.getNextBaseUrl(service);
        
        // handle this
        if(baseUrl == null){
            return null;
        }
        
        return String.format("%s%s", baseUrl, api);
    }

    /**
     * Gets the discovery type
     * 
     * @return Returns the discovery type
     */
    @Override
    public String getType() {
        return "STATIC";
    }

    /**
     * Resolves the instance of service
     * 
     * @param service The service to response
     * @return Returns the service instance
     */
    @Override
    public DiscoveryInstance getInstance(String service) {

        // get port
        var port = this.ports.getOrDefault(service, 0);
    
        // get the host
        var host = this.environment == null ? service : this.environment;
        
        // return instance
        return new DiscoveryInstance(host, port, Str.EMPTY);
    }
}
