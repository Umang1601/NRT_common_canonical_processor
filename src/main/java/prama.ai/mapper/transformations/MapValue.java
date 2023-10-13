package prama.ai.mapper.transformations;

import prama.ai.mapper.model.MappingModel;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapValue<K, V> implements Transformation<Map<K, V>> {

    private Map<Transformation<K>, Transformation<V>> transformations = new LinkedHashMap<>();

    private boolean allowNulls = true;

    public  void put(Transformation<K> key, Transformation<V> value)
    {
        transformations.put(key, value);
    }

    public MapValue(boolean allowNulls) {
        this.allowNulls = allowNulls;
    }

    @Override
    public Map<K, V> apply(Object source)
    {
        System.out.println("In apply method, for transformation");
        Map<K, V> result = new LinkedHashMap<>();

        for(Map.Entry<Transformation<K>, Transformation<V>> en : transformations.entrySet())
        {
            K key = en.getKey().apply(source);
            try{
                MappingModel.recordField(key.toString());
                V value  = en.getValue().apply(source);
                if(allowNulls || value != null)
                {
                    result.put(key, value);
                }
            }
            catch (Exception e)
            {
                System.out.println("Failed to transform key");
            }
            finally {
                MappingModel.clearField(key.toString());
            }
        }

        return result;
    }

    public Map<K, Transformation<V>> getIntermediateMap(Object source) {

        Map<K, Transformation<V>> map = new LinkedHashMap<>();
        for (Map.Entry<Transformation<K>, Transformation<V>> entry : transformations.entrySet()) {
            Transformation<K> key = entry.getKey();
            map.put(key.apply(source), entry.getValue());
        }
        return map;
    }

}

