package prama.ai.mapper.transformations;

public interface Transformation<T> {
    T apply(Object source);
}
