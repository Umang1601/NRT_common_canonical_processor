//package prama.ai.canonical.config;
//
//import javax.annotation.PostConstruct;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.context.support.GenericApplicationContext;
//import org.springframework.core.env.Environment;
//
//@Configuration("customProcessorsConfig")
//@DependsOn({ "dbCanonicalConfiguration" })
//public class CustomProcessorsConfig {
//
//    @Value("${activeConfigurations}")
//    private String activeConfigurations;
//
//    @Autowired
//    private GenericApplicationContext context;
//
//    @Autowired
//    private Environment env;
//
//    @PostConstruct
//    public void init() throws ClassNotFoundException {
//
//        String[] configs = activeConfigurations.split(",");
//        for (String config : configs) {
//            String processorClass = env.getProperty(config + ".processor");
//            if (processorClass != null) {
//                Class<?> clazz = Class.forName(processorClass);
//        //        context.registerBean(clazz);
//            }
//        }
//    }
//}
