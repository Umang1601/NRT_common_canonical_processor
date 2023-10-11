package prama.ai.canonical.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import prama.ai.canonical.config.ProcessorConfig;
import prama.ai.canonical.service.MongoDbClientService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("defaultProcessor")
@DependsOn("appConfig")
public class DefaultProcessor {

    @Value("${db.collection.version:}")
    private String dbCollectionVersion;

    @Autowired
    protected MongoDbClientService mongo;

    public final Map<String, Object> getRawEntities(String id, String collection)
    {
        return mongo.findRawById(id, collection);

    }

    public void process(ProcessorConfig config, Map<String, Object> raw, String collection) {

        Map<String, Object> canonical = createCanonical(config,raw);

        save(config, canonical, collection);
    }

    private Map<String, Object> createCanonical(ProcessorConfig config, Map<String, Object> raw)
    {
        Map<String, Object> canonical = config.getModel().performMapping(raw); // create mapper here;
        return canonical;
    }

    private Map<String, Object> save(ProcessorConfig config, Map<String, Object> canonical, String collection)
    {
        Map<String, Object> keyMap = getKeyMap(config, canonical);

        // not required as we are not doing versioning.
//      String dbCollection = config.getCollection() + config.getCollectionVersion();
        return mongo.saveCanonical(keyMap,canonical,collection);
    }

    private Map<String, Object> getKeyMap(ProcessorConfig config, Map<String, Object> canonical)
    {
        Map<String, Object> keyMap = new HashMap<>();
        for(String key : config.getKeys()) // need to check how it will work
        {
            keyMap.put(key, canonical.get(key));
        }

        return keyMap;
    }


}
