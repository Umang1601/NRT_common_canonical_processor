package prama.ai.mapper.transformations;

import java.util.ArrayList;
import java.util.List;

public class FromPath<T> implements Transformation<T> {

    private String path;

    private List<Transformation<?>> parts = new ArrayList<>();

    public FromPath(String path)
    {
        if(path == null)
        {
            System.out.println("path is null, exception should be thrown");
        }

        path = path.startsWith("$") ? path.substring(1) : path;
        this.path = path;

        for(String part : path.split("\\."))
        {
            if(part.startsWith("[") && part.endsWith("["))
            {
                int index = Integer.valueOf(part.substring(1, part.length() - 1));
                //parts.add(new FromList(index));
            }
            else {
                parts.add(new FromMap(part));
            }
        }
    }

    @Override
    public T apply(Object source) {

        Object result = source;
        for(int i = 0; result != null && i < parts.size(); i++)
        {
            result = parts.get(i).apply(result);
        }
        if(result == null)
        {
            System.out.println("results is null, check further");
            return null;
        }

        return (T) result;
    }
    @Override
    public String toString()
    {
        return "valueOf(" + path + ")";
    }
}
