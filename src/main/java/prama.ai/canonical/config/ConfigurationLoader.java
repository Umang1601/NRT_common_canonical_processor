package prama.ai.canonical.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import prama.ai.canonical.processor.DefaultProcessor;
import prama.ai.mapper.builder.ModelBuilder;
import prama.ai.mapper.model.MappingModel;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@DependsOn("appConfig")
public class ConfigurationLoader {

    @Autowired
    private ApplicationContext context;

    //@Autowired
    //private ApplicationLogger appLogger;

    @Autowired
    MeterRegistry meterrRegistry;

    //    @Value("${activeConfigurations}")
    private String activeConfigurations = "CanonicalPolicyEntity";
    @Value("${db.collection.version:}")
    private String dbCollectionVersion;


    public Map<String, ProcessorConfig> load() throws ClassNotFoundException {
        Map<String, ProcessorConfig> configurations = new HashMap<>();
        System.out.println("Within load method");

        String configName = activeConfigurations.trim();
        System.out.println("Configname " + configName);
        Environment env = context.getEnvironment();
        String topic = env.getProperty(configName + ".topic");
        System.out.println("topic name :  " + topic);

        String collectionName = env.getProperty(configName + ".collection");
        System.out.println("collection name %s" + collectionName);

        String[] keys = getKeys(env, configName);
        System.out.println("Keys are %s " + Arrays.toString(keys));

        String mapperFile = env.getProperty(configName + ".mapper");
        String processorName = env.getProperty(configName + ".processor");

        System.out.println("processor name " + processorName);

        MappingModel model = getMappingModel(mapperFile, configName);
        DefaultProcessor processor = getProcessor(processorName, configName);

        ProcessorConfig configuration = new ProcessorConfig(configName, topic, collectionName, processor, model, keys);

        configurations.put(topic, configuration);

        return configurations;
    }

    private String[] getKeys(Environment env, String configName) {
        String[] keys = env.getProperty(configName + ".keys").split((","));
        for (int i = 0; i < keys.length; i++) {
            keys[i] = keys[i].trim();
        }

        return keys;
    }

    private MappingModel getMappingModel(String mapperFile, String configName) {

        // String mapperFile = env.getProperty((configName + ".mapper"));

        System.out.println("within mapping model method and mapper file %s" + mapperFile);
        // not yet set in config, currently hardcoded to send as true
        // boolean allowNulls = !"true".equals(env.getProperty(configName + ".removeNulls"));
        if (mapperFile != null) {
            InputStream resource = getClass().getClassLoader().getResourceAsStream(mapperFile);

            ModelBuilder builder = new ModelBuilder(configName, resource, false);

            return builder.build();
        }
        return null;
    }

    private DefaultProcessor getProcessor(String processorName, String configName) throws ClassNotFoundException {
        //String processorClass = env.getProperty(configName + ".processor");
        return createProcessor(processorName);
    }

    private DefaultProcessor createProcessor(String processorClass) throws ClassNotFoundException {
        DefaultProcessor processor;
        if (processorClass != null && !processorClass.isEmpty()) {
            processor = (DefaultProcessor) context.getBean(Class.forName(processorClass));
            log.info("Found Custom processor " + processorClass);
        } else {
            processor = context.getBean("defaultProcessor", DefaultProcessor.class);
        }

        return processor;
    }

}
