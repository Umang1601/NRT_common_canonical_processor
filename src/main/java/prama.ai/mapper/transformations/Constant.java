package prama.ai.mapper.transformations;


public class Constant<T> implements Transformation<T> {
    private T value;

    public Constant(T value)
    {
        this.value = value;
    }

    @Override
    public T apply(Object source) {
        return value;
    }
    @Override
    public  String toString()
    {
        return value instanceof String ? "'" + value.toString() + "'" : value.toString();
    }
}
