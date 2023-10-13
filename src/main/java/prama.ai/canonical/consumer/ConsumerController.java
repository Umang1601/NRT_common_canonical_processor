//package prama.ai.canonical.consumer;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import prama.ai.canonical.config.ConfigurationLoader;
//import prama.ai.canonical.config.ProcessorConfig;
//import prama.ai.canonical.processor.DefaultProcessor;
//
//import java.lang.module.Configuration;
//import java.util.Map;
//
//@RestController
//@RequestMapping()
//public class ConsumerController {
//
//    @Autowired
//    DefaultProcessor processor;
//
//    @Autowired
//    ConfigurationLoader loader;
//
//    @GetMapping("/consumer")
//    public String getConsumer() {
//        System.out.println("Consumer ");
//        String id = "123";
//        String collection = "Policy";
//        Map<String, Object> rawEntities = processor.getRawEntities(id, collection);
//        System.out.println("Raw Entities received %s " + rawEntities);
//        String topic = "sample.entity.processing.topic";
//        Map<String, ProcessorConfig> configurations;
//        ProcessorConfig config = null;
//        try {
//            configurations = loader.load();
//            config = configurations.get(topic);
//        }
//        catch (Exception e)
//        {
//            System.out.println("Received Exception");
//        }
//        processor.process(config, rawEntities, collection);
//        return "Success";
//    }
//
//}
