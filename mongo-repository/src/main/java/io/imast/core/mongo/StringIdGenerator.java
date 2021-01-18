package io.imast.core.mongo;

import org.bson.codecs.pojo.IdGenerator;
import org.bson.types.ObjectId;

/**
 * The String Identity Generator
 * 
 * @author davitp
 */
public class StringIdGenerator implements IdGenerator<String>{
    
    /**
     * Generate next identifier
     * 
     * @return Returns next string id
     */
    @Override
    public String generate() {
        return ObjectId.get().toHexString();
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}
