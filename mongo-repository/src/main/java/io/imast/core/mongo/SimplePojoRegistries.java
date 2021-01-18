package io.imast.core.mongo;

import com.mongodb.MongoClientSettings;
import io.imast.core.mongo.codecs.ZdtCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.PojoCodecProvider;

/**
 * A helper class to provide simple access to POJO configuration
 * 
 * @author davitp
 */
public class SimplePojoRegistries {
   
    /**
     * Gets the simple registry
     * 
     * @param models The class models
     * @return Returns simple POJO Registry
     */
    public static CodecRegistry simple(ClassModel<?>... models){
        return CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(new ZdtCodec()),
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().register(models).automatic(true).build())
        );
    }
    
}
