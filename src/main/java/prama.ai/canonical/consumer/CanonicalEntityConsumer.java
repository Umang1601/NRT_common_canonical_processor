package prama.ai.canonical.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import prama.ai.canonical.config.ConfigurationLoader;
import prama.ai.canonical.config.ProcessorConfig;
import prama.ai.canonical.processor.DefaultProcessor;
import prama.ai.canonical.util.GenericRecordUtil;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.DependsOn;


@Component
@Slf4j
//@DependsOn("customProcessorsConfig")
public class CanonicalEntityConsumer {

    private Map<String, Configuration> configurations = new HashMap<>();

    //@Value("${activeConfigurations}")
    private String activeConfigurations = "CanonicalSampleEntity";

    private static final String DOCUMENT_ID = "DOCUMENT_ID";

    private static final String MONGODB_COLLECTION_NAME = "MONGODB_COLLECTION_NAME";

    @Autowired
    private ApplicationContext context;
    @Autowired
    private MeterRegistry meterRegistry;
    @Autowired
    private ConfigurationLoader loader;

    @Autowired
    MongoTemplate mongoTemplate;

//	@Autowired
//	private MessageProcessor processor;

    //@KafkaListener(topics="#{'${inputTopics}'.split(',')",groupId="${app.kafkaApplicationId}")
   @KafkaListener(topics="sample.entity.raw.topic", groupId ="NRT-common-raw-consumer-app")
    public void processMessage(@Header(KafkaHeaders.RECEIVED_TOPIC)final String topic, ConsumerRecord<Void, GenericRecord> message
                               //ConsumerRecord<Void, GenericRecord> record
    ) {
        //final Configuration config = configurations.get(topic);
        System.out.println("topic : "+topic);

       Map<String, ProcessorConfig> configurations;
       ProcessorConfig config = null;
        try {

            configurations = loader.load();
             config = configurations.get(topic);
        }
        catch (Exception e)
        {
            System.out.println("Received Exception during configuration loader");
        }
        try {
            GenericRecord record = message.value();
            System.out.println("message received from kafka %s" + record);

            JsonNode json = GenericRecordUtil.toJson(record);
            String id = json.get(DOCUMENT_ID).asText();
            String collection = json.get(MONGODB_COLLECTION_NAME).asText();

            System.out.println(" id of rawdb %s and collection name %s "+ id + collection);

            // process the data based on id and collection name
            processCanonicalEntity(topic,id, collection, config);

        }
        catch (Exception e)
        {
            System.out.println("Exception caught in Canonical Consumer");
        }
    }

    private void processCanonicalEntity(String topic, String id, String collection, ProcessorConfig config)
    {
        DefaultProcessor defaultProcessor = config.getProcessor();
        Map<String, Object> raw = defaultProcessor.getRawEntities(id, collection);

        System.out.print("raw entities received from rawdb " +raw);
        defaultProcessor.process(config,raw, collection);
    }


}
