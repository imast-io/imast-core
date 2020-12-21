package io.imast.core.discovery;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.imast.core.Str;
import lombok.extern.slf4j.Slf4j;

/**
 * A eureka-based discovery client
 * 
 * @author davitp
 */
@Slf4j
public class EurekaDiscoveryClient implements DiscoveryClient {
    
    /**
     * The eureka client
     */
    private final EurekaClient eurekaClient;
    
    /**
     * The fallback discovery client
     */
    private final DiscoveryClient fallbackDiscovery;

    /**
     * Creates new eureka discovery client
     * 
     * @param eurekaClient The eureka client
     * @param fallbackDiscovery The fallback discovery
     */
    public EurekaDiscoveryClient(EurekaClient eurekaClient, DiscoveryClient fallbackDiscovery){
        this.eurekaClient = eurekaClient;
        this.fallbackDiscovery = fallbackDiscovery;
    }
    
    /**
     * Gets the next available instance info
     * 
     * @param service The service name
     * @return Returns the instance
     */
    private InstanceInfo getNextInstance(String service){
        try{
            // try get instance from 
            return this.eurekaClient.getNextServerFromEureka(service, false);
        }
        catch(Exception e){
            log.error("Eureka Discovery: Could not execute getNextServerFromEureka: " + e.getLocalizedMessage());
        }
        
        return null;
    }
    
    /**
     * Gets next base URL from eureka if available
     * 
     * @param service The service to resolve
     * @return Returns eureka-resolved URL
     */
    @Override
    public String getNextBaseUrl(String service) {
        
        // the base URL
        String baseUrl = null;
        
        // get instance
        var instance = this.getNextInstance(service);
        
        // set base uri if available
        if(instance != null){
            baseUrl = instance.getHomePageUrl();
        }
        
        // use fallback discovery in this case
        if(baseUrl == null && this.fallbackDiscovery != null){
            return this.fallbackDiscovery.getNextBaseUrl(service);
        }
        
        return baseUrl;
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
     * The type of discovery
     * 
     * @return The discovery type
     */
    @Override
    public String getType() {
        return "EUREKA";
    }

    /**
     * Resolves the instance of service
     * 
     * @param service The service to response
     * @return Returns the service instance
     */
    @Override
    public DiscoveryInstance getInstance(String service) {
        
        // get instance if available
        var eurekaInstance = this.getNextInstance(service);
        
        // if instance is there
        if(eurekaInstance != null){
            return new DiscoveryInstance(eurekaInstance.getHostName(), eurekaInstance.getPort(), Str.EMPTY);
        }
        
        // check if fallback discovery is given
        if(this.fallbackDiscovery != null){
            return this.fallbackDiscovery.getInstance(service);
        }
        
        return null;
    }
}
