package prama.ai.mapper.model;

import prama.ai.mapper.transformations.MapValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

public class MappingModel {

    private String name;

    private MapValue<String, Object> variables;

    private MapValue<String, Object> transformation;

    private static final ThreadLocal<Stack<String>> currentField = new ThreadLocal<>();

    public MappingModel(String name, MapValue<String, Object> variables, MapValue<String, Object> transformation) {
        this.name = name;
        this.variables = variables;
        this.transformation = transformation;
    }

    public static void clearField(String string) {
        // clear string when exception occurs. Not required now
    }

    public static void recordField(String field) {
        currentField.get().push(field);
    }

    public Map<String, Object> performMapping(Map<String, Object> raw)
    {
        reset();
        currentField.set(new Stack<>());

        //not required to do validation as of now

        Map<String, Object> intermediateMap = new LinkedHashMap<>();
        intermediateMap.putAll(raw);

        if(variables!=null)
        {
            intermediateMap.putAll(variables.apply(raw));
        }

        return transformation.apply(intermediateMap);
    }

    private void reset()
    {
        currentField.remove();
    }
}

