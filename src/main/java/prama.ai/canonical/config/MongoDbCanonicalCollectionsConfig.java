package prama.ai.canonical.config;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.HashedIndex;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.yaml.snakeyaml.Yaml;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@DependsOn("dbCanonicalConfiguration")
@ConditionalOnProperty(name = "execution-type", havingValue = "deployed", matchIfMissing = true)
public class MongoDbCanonicalCollectionsConfig {

    @Value("${db.collection.version:}")
    private String dbCollectionVersion;

    @Autowired
    @Qualifier("canonicalMongoTemplate")
    private MongoTemplate canonicalTemplate;

    @PostConstruct
    public void initDatabase() {
        String filePath = "V1".equals(dbCollectionVersion) ? "db-collections-v1.yaml" : "db-collections.yaml";
        Yaml yaml = new Yaml();
        InputStream fileStream = prama.ai.canonical.config.MongoDbCanonicalCollectionsConfig.class.getClassLoader().getResourceAsStream("db-collections.yaml");

        Map<String, Object> properties = yaml.load(fileStream);
        List<Object> canonicalCollections = (List<Object>) properties.get("canonical");

        prepareCollections(canonicalTemplate, canonicalCollections);
    }

    protected void prepareCollections(MongoTemplate mongo, List<Object> collections) {
        if (collections != null && !collections.isEmpty()) {
            for (Object collection : collections) {
                prepareCollection(mongo, collection);
            }
        }
    }

    protected void prepareCollection(MongoTemplate mongo, Object collection) {
        if (collection != null) {
            if (collection instanceof String) {
                createCollection(mongo, (String) collection);
            } else if (collection instanceof Map) {

                Map<String, List<Object>> collectionMap = (Map<String, List<Object>>) collection;

                String collectionName = getSingleKey(collectionMap);
                List<Object> indexes = collectionMap.get(collectionName);

                for (Object index : indexes) {
                    if (index instanceof Map) {
                        ensureIndex(mongo, collectionName, (Map<String, Object>) index);
                    } else {
                        throw new RuntimeException("Misconfigured index : " + index + " for collection" + collectionName
                                + "in Database:" + mongo.getDb().getName());
                    }
                }
            }
        }
    }

    private void ensureIndex(MongoTemplate mongo, String collection, Map<String, Object> indexMap) {
        String name = (String) indexMap.get("Name");
        String[] fields = ((String) indexMap.get("fields")).split(",");
        String type = (String) indexMap.get("type");
        Object uniqueObj = indexMap.get("unique");
        boolean isUnique = uniqueObj == null ? false : (boolean) uniqueObj;

        IndexDefinition index = null;

        switch (type) {
            case "asc":
            case "desc":
                Sort.Direction direction = type.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
                Index ci = new Index();

                for (String field : fields) {
                    ci.on(field, direction);
                }
                index = isUnique ? ci.unique() : ci;
                break;

            case "hashed":
                if (fields.length == 1) {
                    HashedIndex hashed = HashedIndex.hashed(type);
                    index = hashed;
                } else {
                    throw new RuntimeException(
                            "Hashed index: " + name + " on multiple is not possible, configured for fields: " + collection
                                    + " in Database " + mongo.getDb().getName());
                }
                break;

            case "text":
                TextIndexDefinition.TextIndexDefinitionBuilder b = new TextIndexDefinition.TextIndexDefinitionBuilder();
                b.onFields(fields);
                b.named(name);
                index = b.build();
                break;

            default:
                throw new RuntimeException(
                        "Misconfigured index " + name + " of type " + type + " on field " + Arrays.asList(fields)
                                + " for colelction " + collection + " in Database " + mongo.getDb().getName());
        }
        log.info("Ensuring Index " + indexMap + " in collection " + collection);
        mongo.indexOps(collection).ensureIndex(index);
    }

    protected void createCollection(MongoTemplate mongo, String collection) {
        if (!mongo.getCollectionNames().contains(collection)) {
            log.info("creating missing collection " + collection);
        }
    }

    private String getSingleKey(Map<String, ?> map) {
        return map.keySet().iterator().next();
    }

}

