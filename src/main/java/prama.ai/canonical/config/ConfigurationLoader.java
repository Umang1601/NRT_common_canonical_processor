package prama.ai.canonical.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import prama.ai.canonical.processor.DefaultProcessor;
import prama.ai.mapper.builder.ModelBuilder;
import prama.ai.mapper.model.MappingModel;

import java.io.InputStream;
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

    MeterRegistry meterrRegistry;

//    @Value("${activeConfigurations}")
    private String activeConfigurations = "CanonicalSampleEntity";
    @Value("${db.collection.version:}")
    private String dbCollectionVersion;

    public Map<String, ProcessorConfig> load() throws ClassNotFoundException{
        Map<String, ProcessorConfig> configurations = new HashMap<>();

        Environment env = context.getEnvironment();
        for(String configName : activeConfigurations.split(","))
        {
            configName = configName.trim();
            String topic = env.getProperty(configName + ".topic");
            String collection = env.getProperty(configName + ".collection");
            String[] keys = getKeys(env, configName);
            MappingModel model = getMappingModel(env, configName);
            DefaultProcessor processor = getProcessor(env, configName);

            ProcessorConfig configuration = new ProcessorConfig(configName, topic, collection,  processor, model, keys );

            configurations.put(topic, configuration);

        }

        return configurations;
    }

    private String[] getKeys(Environment env, String configName)
    {
        String[] keys = env.getProperty(configName + ".keys").split((","));
        for(int i =0; i< keys.length; i++)
        {
            keys[i] = keys[i].trim();
        }

        return keys;
    }

    private MappingModel getMappingModel(Environment env, String configName)
    {
        String mapperFile = env.getProperty((configName + ".mapper"));
        // not yet set in config, currently hardcoded to send as true
        // boolean allowNulls = !"true".equals(env.getProperty(configName + ".removeNulls"));
        if(mapperFile != null)
        {
            InputStream resource = getClass().getClassLoader().getResourceAsStream(mapperFile);

            ModelBuilder builder = new ModelBuilder(configName, resource, true);

            return builder.build();
        }
        return null;
    }

    private DefaultProcessor getProcessor(Environment env, String configName) throws ClassNotFoundException
    {
        String processorClass = env.getProperty(configName + ".processor");
        return createProcessor(processorClass);
    }

    private DefaultProcessor createProcessor(String processorClass) throws ClassNotFoundException
    {
        DefaultProcessor processor;
        if(processorClass != null && !processorClass.isEmpty())
        {
            processor = (DefaultProcessor) context.getBean(Class.forName(processorClass));
            log.info("Found Custome processor " + processorClass);
        }
        else {
            processor = context.getBean("defaultProcessor", DefaultProcessor.class);
        }

        return processor;
    }

}
