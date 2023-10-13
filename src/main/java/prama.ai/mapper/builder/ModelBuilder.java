package prama.ai.mapper.builder;

import org.yaml.snakeyaml.Yaml;
import prama.ai.mapper.annotation.Transformer;
import prama.ai.mapper.model.MappingModel;
import prama.ai.mapper.transformations.*;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class ModelBuilder {

    private static final Map<String, Class<Transformation>> transformers = new HashMap<>();
    static
    {
        initTransformers("prama.ai.mapper", Transformer.class);
    }

    private final String name;

    private final InputStream stream;

    private MapValue<String, Object> variables;

    // validations is not yet implemented
    // private ListValue<Boolean> validations;

    public final  boolean allowNulls;

    public  ModelBuilder(String name, InputStream stream)
    {
        this(name, stream, true);
    }

    public  ModelBuilder(String name, InputStream stream, boolean allowNulls)
    {
        this.name = name;
        this.stream = stream;
        this.allowNulls = allowNulls;
    }

    public MappingModel build()
    {
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.load(stream);

        MapValue<String, Object> transformer = (MapValue<String, Object>) getModel(map);
        MappingModel mappingModel = new MappingModel(name, variables, transformer);
        return mappingModel;
    }

    private Transformation getModel(Object val)
    {
        if(val == null)
        {
            return null;
        }
        if(val instanceof String)
        {
            String str = (String) val;
            if(str.startsWith("$$"))
            {
                Transformation transformer = getTransformerInstance(str, null);
                if(transformer != null)
                {
                    return transformer;
                }
                else {
                    System.out.println("Transformer instance is null ");
                }
            } else if (str.startsWith("$")) {
                System.out.println("FromPath method");
                return new FromPath<>(str);
            }
            System.out.println("constant method");
            return new Constant<>(val);

        } else if (val instanceof Map) {
            return createModelFromMap((Map<?,?>) val);
        }
        else if (val instanceof List) {
            return createModelFromList((List<?>) val);
        } else if (val instanceof Number || val instanceof Boolean) {
            return new Constant<>(val);
        }
        System.out.println("provide mapper -> none of the instanceOf matched with val");
         return null;
    }

    private Transformation createModelFromList(List<?> list) {

        ListValue<Object> model = new ListValue<>();
        list.forEach(v -> model.add(getModel(v)));
        return model;
    }

    private Transformation createModelFromMap(Map<?,?> map)
    {
        if(map.keySet().size() == 1)
        {
            Object key  = map.keySet().toArray()[0];
            Transformation valueModel = getModel(map.get(key));
            Transformation transformer = getTransformerInstance(key, valueModel);
            if(transformer!=null)
            {
                return transformer;
            }
        }
        return createMapModel(map);
    }

    private Transformation createMapModel(Map<?,?> map)
    {
        MapValue model = new MapValue<>(true);
        for(Map.Entry<?,?> entry : map.entrySet())
        {
            Object key = entry.getKey();
            Object value = entry.getValue();
            Transformation valueModel = getModel(value);

            if("$$validations".equals(key))
            {
                System.out.println("validation not yet implemented");
            }
            else if ("$$variables".equals(key))
            {
                variables = (MapValue<String, Object>) valueModel;
            }
            else {
                model.put(getModel(key),valueModel);
            }

        }
        return model;
    }

    private Transformation getTransformerInstance(Object key, Transformation cstrParam)
    {
        try{
            if(transformers.containsKey(key))
            {
                Class<Transformation> clazz = transformers.get(key);
                Constructor<Transformation> cstr = clazz.getDeclaredConstructor(Transformation.class);
                return cstr.newInstance(cstrParam);
            }
        }
        catch (Exception e)
        {
            System.out.println("Exception caught in transformerInstance method");
        }
        return null;
    }

    private static <T extends Annotation> void initTransformers(String basePackage, Class<T> annotationClass)
    {
        Reflections ref = new Reflections(basePackage);
        Set<Class<?>> types = ref.getTypesAnnotatedWith(annotationClass);
        for(Class<?> clazz : types)
        {
            Transformer transformer = clazz.getAnnotation(Transformer.class);
            transformers.put(transformer.value(), (Class<Transformation>) clazz);
        }
        System.out.println("\n Supported keywords are : " + transformers.keySet() + "\n");

    }

}
