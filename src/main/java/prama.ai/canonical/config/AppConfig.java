package prama.ai.canonical.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.annotation.PostConstruct;

@Configuration("appConfig")
@ConditionalOnProperty(name = "execution-type", havingValue = "deployed", matchIfMissing = true)
public class AppConfig {

    @Autowired
    private ConfigurableEnvironment env;

    @PostConstruct
    public void initProperties()
    {
        configureAwsProperties();
        configureInputTopics();
    }

    private void configureAwsProperties()
    {
        //need to check whether its really required
    }

    private void configureInputTopics()
    {
        //again check is it really required. I dont think so.
//        String topicKeys = (String) env.getProperty("activeConfigurations");
//        String[] keys = topicKeys.split(",");
    }
}
