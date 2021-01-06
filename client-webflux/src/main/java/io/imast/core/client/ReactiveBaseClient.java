package io.imast.core.client;

import io.imast.core.discovery.DiscoveryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The Reactive-ready base class for client module implementation
 * 
 * @author davitp
 */
@Slf4j
public class ReactiveBaseClient extends BaseClient {
    
    /**
     * The web client 
     */
    protected final WebClient webClient;
    
    /**
     * The base client for Wisdom Services
     * 
     * @param client The client name
     * @param service The service
     * @param discovery The discovery module
     * @param exchangeStrategies The exchange strategies
     */
    public ReactiveBaseClient(String client, String service, DiscoveryClient discovery, ExchangeStrategies exchangeStrategies){
        super(client, service, discovery);
        
        // the builder
        var builder = WebClient.builder();
        
        // set exchange strategies
        if(exchangeStrategies != null){
            builder.exchangeStrategies(exchangeStrategies);
        }
        
        // set default headers
        builder
            .defaultHeader(CLIENT_HEADER, this.client)
            .defaultHeader(CONTENT_TYPE_HEADER, JSON_MEDIA);
        
        this.webClient = builder.build();
    }
    
    /**
     * The base client for Wisdom Services
     * 
     * @param client The client name
     * @param service The service
     * @param discovery The discovery module
     */
    public ReactiveBaseClient(String client, String service, DiscoveryClient discovery){
        this(client, service, discovery, null);
    }    
}