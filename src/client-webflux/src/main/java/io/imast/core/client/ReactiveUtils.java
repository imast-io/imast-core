package io.imast.core.client;

import io.vavr.control.Try;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * The reactive attribute implementation
 * 
 * @author davitp
 */
@Slf4j
public class ReactiveUtils {
    
    /**
     * Block and return optional result
     * 
     * @param <T> The type of response
     * @param publisher The publisher
     * @return Returns optional response
     */
    public static <T> Optional<T> blockOptional(Mono<T> publisher){
        
        // try get result from mono
        Try<T> tryGet = Try.of(() -> publisher.block());
        
        // handle success
        if(tryGet.isSuccess()){
            return Optional.ofNullable(tryGet.get());
        }
        
        // on error
        log.error("Error while executing client operation: " + tryGet.getCause().getLocalizedMessage());
        return Optional.empty();
    }
    
    /**
     * Block and return optional result
     * 
     * @param <T> The type of response
     * @param publisher The publisher
     * @param onError Do on error
     * @return Returns optional response
     */
    public static <T> Optional<T> blockOptional(Mono<T> publisher, Consumer<Throwable> onError){
        
        // try get result from mono
        Try<T> tryGet = Try.of(() -> publisher.block());
        
        // handle success
        if(tryGet.isSuccess()){
            return Optional.ofNullable(tryGet.get());
        }
        
        // handle error
        if(onError != null){
            onError.accept(tryGet.getCause());
        }
        
        // on error
        log.error("Error while executing client operation: " + tryGet.getCause().getLocalizedMessage());
        return Optional.empty();
    }
    
    /**
     * Block and return optional result
     * 
     * @param <T> The type of response
     * @param publisher The publisher
     * @param defaultValue The default value
     * @return Returns optional response
     */
    public static <T> T block(Mono<T> publisher, T defaultValue){
        
        // try get result from mono
        Try<T> tryGet = Try.of(() -> publisher.block());
        
        // handle success
        if(tryGet.isSuccess()){
            return tryGet.get();
        }
        
        // on error
        log.error("Error while executing client operation: " + tryGet.getCause().getLocalizedMessage() + ". Using default value instead. ");
        return defaultValue;
    }
    
    /**
     * Block and return optional result
     * 
     * @param <T> The type of response
     * @param publisher The publisher
     * @param supplier The otherwise supplier
     * @return Returns optional response
     */
    public static <T> T blockOr(Mono<T> publisher, Function<Throwable, T> supplier){
        
        // try get result from mono
        Try<T> tryGet = Try.of(() -> publisher.block());
        
        // handle success
        if(tryGet.isSuccess()){
            return tryGet.get();
        }
        
        // on error
        log.error("Error while executing client operation: " + tryGet.getCause().getLocalizedMessage() + ". Using default value instead. ");
        return supplier.apply(tryGet.getCause());
    }
}
