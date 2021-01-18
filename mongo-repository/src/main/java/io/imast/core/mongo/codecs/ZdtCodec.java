package io.imast.core.mongo.codecs;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import static java.util.Objects.requireNonNull;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * The Codec for Zoned Date Time
 * 
 * @author davitp
 */
public class ZdtCodec implements Codec<ZonedDateTime> {

    /**
     * Encodes zoned date time into a Date Time mongo field
     * 
     * @param writer The writer instance
     * @param value The value to write
     * @param encoderContext The encoder context
     */
    @Override
    public void encode(BsonWriter writer, ZonedDateTime value, EncoderContext encoderContext) {
        
        // make sure there is a writer
        requireNonNull(writer, "writer is null");
        
        // null is fine
        if(value == null){
            writer.writeNull();
            return;
        }
        
        writer.writeDateTime(value.toInstant().toEpochMilli());
    }

    /**
     * Gets encoder class
     * 
     * @return Returns encoder class
     */
    @Override
    public Class<ZonedDateTime> getEncoderClass() {
        return ZonedDateTime.class;
    }

    /**
     * Decodes zoned date time from Date Time field
     * 
     * @param reader The reader object
     * @param decoderContext The decoder context
     * @return Returns zoned date time
     */
    @Override
    public ZonedDateTime decode(BsonReader reader, DecoderContext decoderContext) {

        // make sure reader is there
        requireNonNull(reader, "reader is null");
        
        // decode zoned date time
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(reader.readDateTime()), ZoneOffset.UTC);
    }
    
}
