package prama.ai.mapper.transformations;

import java.util.Map;

public class FromMap implements Transformation<Object> {

    private String key;

    public FromMap(String key) {
        this.key = key;
    }

    @Override
    public Object apply(Object source)
    {
        if(source != null && source instanceof Map)
        {
            return ((Map) source).get(key);
        }
        return null;
    }
}
