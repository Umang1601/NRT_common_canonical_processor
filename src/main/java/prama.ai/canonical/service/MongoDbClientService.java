package prama.ai.canonical.service;


import io.micrometer.core.annotation.Timed;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MongoDbClientService {

    @Autowired
    @Qualifier("rawMongoTemplate")
    private MongoTemplate rawTemplate;

    @Autowired
    @Qualifier("canonicalMongoTemplate")
    private MongoTemplate canonicalTemplate;

    @Timed(value = "custom.mongodb.findRawById")
    public Map<String, Object> findRawById(String id, String collection) {
        return  rawTemplate.findById(id, Document.class, collection);
    }

    @Timed(value = "custom.mongodb.saveCanonical")
    public Map<String, Object> saveCanonical(Map<String, Object> keyMap, Map<String, Object> canonicalDocument, String collectionName) {

        Query query = getQuery(keyMap); // need to check what it is doing
        FindAndReplaceOptions option = new FindAndReplaceOptions().upsert().returnNew();

        Map<String, Object> result = canonicalTemplate.findAndReplace(query, canonicalDocument, option, collectionName );
        return result;
    }

    public static Query getQuery(Map<String, Object> keyMap)
    {
        return getQuery(keyMap, false);
    }

    public static Query getQuery(Map<String, Object> keyMap, boolean excludeDeleted)
    {
        List<Criteria> criteriaList = new ArrayList<>();
        for(Map.Entry<String, Object> entry : keyMap.entrySet())
        {
            criteriaList.add(Criteria.where(entry.getKey()).is(entry.getValue()));
        }
        // add new block for excludedeleted, not needed now.

        Criteria criteria = criteriaList.isEmpty() ? null : new Criteria().andOperator(criteriaList);

        Query query = new Query(criteria);
        return query;
    }
}
