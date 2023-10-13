package prama.ai.canonical.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration("dbCanonicalConfiguration")
//@DependsOn("propertiesConfig")
@ConditionalOnProperty(name = "execution-type", havingValue = "deployed", matchIfMissing = true)
public class MongoDbCanonicalConfiguration {

    @Autowired
    private ConfigurableEnvironment env;

    @Primary
    @Bean(name = "rawMongoTemplate")
    MongoTemplate rawMongoTemplate() throws Exception {
        return new MongoTemplate(rawFactory());
    }

    @Primary
    @Bean
    MongoDatabaseFactory rawFactory() throws Exception {
        return new SimpleMongoClientDatabaseFactory(rawMongoClient(), "rawDB");
    }

    @Bean
    public MongoClient rawMongoClient() throws Exception {

        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyToConnectionPoolSettings(builder1 -> {
            builder1.minSize(0);
            builder1.maxSize(8);
            builder1.maxConnectionIdleTime(1, TimeUnit.MINUTES);

        });
//		builder.applyConnectionString(new ConnectionString(env.getProperty("app.mongodbUrl")));
        builder.applyConnectionString(new ConnectionString("mongodb://localhost:27017"));
        MongoClientSettings clientSettings = builder.build();
        return MongoClients.create(clientSettings);
    }

    @Bean(name = "canonicalMongoTemplate")
    MongoTemplate canonicalMongoTemplate() throws Exception {
        return new MongoTemplate(canonicalFactory());
    }

    @Bean
    MongoDatabaseFactory canonicalFactory() throws Exception {
        return new SimpleMongoClientDatabaseFactory(canonicalMongoClient(), "prama_ai_canonical");
    }

    @Bean
    public MongoClient canonicalMongoClient() throws Exception {

        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyToConnectionPoolSettings(builder1 -> {
            builder1.minSize(0);
            builder1.maxSize(8);
            builder1.maxConnectionIdleTime(1, TimeUnit.MINUTES);

        });
//		builder.applyConnectionString(new ConnectionString(env.getProperty("app.mongodbUrl")));
        builder.applyConnectionString(new ConnectionString("mongodb://localhost:27017"));
        MongoClientSettings clientSettings = builder.build();
        return MongoClients.create(clientSettings);
    }
}