package prama.ai.canonical.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import prama.ai.canonical.config.ProcessorConfig;
import prama.ai.canonical.processor.DefaultProcessor;
import prama.ai.canonical.util.GenericRecordUtil;

import java.util.HashMap;
import java.util.Map;

@Component
public class CanonicalEntityConsumer {

  //  private Map<String, Configuration> configurations = new HashMap<>();

    //@Value("${activeConfigurations}")
    private String activeConfigurations = "SampleEntity";

    private static final String DOCUMENT_ID = "DOCUMENT_ID";

    private static final String MONGODB_COLLECTION_NAME = "MONGODB_COLLECTION_NAME";

    @Autowired
    private ApplicationContext context;
    @Autowired
    private MeterRegistry meterRegistry;
    @Autowired
    private static ProcessorConfig config;

    @Autowired
    MongoTemplate mongoTemplate;

//	@Autowired
//	private MessageProcessor processor;

//	@PostConstruct
//	public void afterPropertiesSet() {
//		for (String conf : activeConfigurations.split(",")) {
//			Environment env = context.getEnvironment();
//			String topic = env.getProperty(conf + ".rawTopic");
//			configurations.put(topic, new Configuration(env, conf, meterRegistry));
//		}
//		configurations = Map.copyOf(configurations);
//	}

    //@KafkaListener(topics="#{'${inputTopics}'.split(',')",groupId="${app.kafkaApplicationId}")
    @KafkaListener(topics="sample.entity.raw.topic", groupId ="NRT-common-raw-consumer-app")
    public void processMessage(@Header(KafkaHeaders.RECEIVED_TOPIC)final String topic, ConsumerRecord<Void, GenericRecord> message
                               //ConsumerRecord<Void, GenericRecord> record
    ) {
        //final Configuration config = configurations.get(topic);
        System.out.println("topic: "+message);

        try {
            GenericRecord record = message.value();

            JsonNode json = GenericRecordUtil.toJson(record);
            String id = json.get(DOCUMENT_ID).asText();
            String collection = json.get(MONGODB_COLLECTION_NAME).asText();

            // process the data based on id and collection name
            processCanonicalEntity(topic,id, collection);

        }
        catch (Exception e)
        {
            System.out.println("Exception caught in Canonical Consumer");
        }

    }





    private void processCanonicalEntity(String topic, String id, String collection)
    {
        DefaultProcessor defaultProcessor = config.getProcessor();
        Map<String, Object> raw = defaultProcessor.getRawEntities(id, collection);
        defaultProcessor.process(config,raw, collection);
    }


}
