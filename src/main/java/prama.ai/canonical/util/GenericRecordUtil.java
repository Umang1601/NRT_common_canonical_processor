package prama.ai.canonical.util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.lang.Nullable;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class GenericRecordUtil {

    public static JsonNode toJson(GenericRecord record)
    {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Encoder encoder = EncoderFactory.get().jsonEncoder(record.getSchema(), outputStream);
            GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(record.getSchema());
            writer.write(record, encoder);
            encoder.flush();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(outputStream.toByteArray());
            fixAvroSubdocuments(record, json);
            return json;
        }
        catch (IOException e)
        {
            throw  new IllegalStateException(e);
        }
    }

    private static JsonNode fixAvroSubdocuments(GenericRecord record, JsonNode json)
    {
        for(Schema.Field f : record.getSchema().getFields())
        {
            JsonNode oldValue;
            if(f.schema().isUnion() && (oldValue = json.get(f.name())) != null && !(oldValue instanceof Nullable))
            {
                Iterator<JsonNode> itr = oldValue.elements();
                if(itr.hasNext())
                {
                    JsonNode newValue = itr.next();
                    ((ObjectNode) json).replace(f.name(), newValue);
                }
            }
        }
        return json;
    }
}