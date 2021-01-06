package io.imast.core.discovery;

import io.imast.core.Str;
import io.vavr.control.Try;
import java.net.URL;

/**
 * A discovery client based on gateway details
 * 
 * @author davitp
 */
public class GatewayDiscoveryClient implements DiscoveryClient {
    
    /**
     * The protocol
     */
    private final String proto;
    
    /**
     * The host name of service
     */
    private final String hostname;
    
    /**
     * The port of service
     */
    private final Integer port;
    
    /**
     * The base path for gateway
     */
    private final String base;
    
    /**
     * Creates new gateway discovery
     * 
     * @param gateway The gateway
     */
    public GatewayDiscoveryClient(String gateway){
        
        // the host
        var givenHost = Str.EMPTY;
        
        // the port
        var givenPort = -1;
        
        // the protocol
        var protocol = "http";
        
        // base is empty by default
        var file = Str.EMPTY;
        
        // try get URL
        var url = Try.of(() -> new URL(gateway)).getOrNull();
        
        // check if url is null
        if(url != null){
            givenHost = url.getHost();
            givenPort = url.getPort();
            protocol = url.getProtocol();
            file = url.getFile();
        }
                
        // remove first slash if given
        if(!Str.blank(file) && file.startsWith("/")){
            file = file.substring(1);
        }
        
        // add last slash if not given
        if(!Str.blank(file) && !file.endsWith("/")){
            file = file + "/";
        }
        
        // set port if unknown
        if(givenPort == -1){
            givenPort = Str.eqIgnoreCase(protocol, "http") ? 80 : 443;
        }
        
        // fallback to localhost
        if(Str.blank(givenHost)){
            givenHost = "localhost";
        }
        
        this.proto = protocol;
        this.hostname = givenHost;
        this.port = givenPort;
        this.base = file;
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
        return String.format("%s://%s:%s/%s", this.proto, instance.getHost(), instance.getPort(), instance.getBase());
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
        
        return String.format("%s%s/%s", baseUrl, service, api);
    }

    /**
     * Gets the discovery type
     * 
     * @return Returns the discovery type
     */
    @Override
    public String getType() {
        return "GATEWAY";
    }

    /**
     * Resolves the instance of service
     * 
     * @param service The service to response
     * @return Returns the service instance
     */
    @Override
    public DiscoveryInstance getInstance(String service) {
        // return instance
        return new DiscoveryInstance(this.hostname, this.port, this.base);
    }
}
