package prama.ai.mapper.transformations.predicates;

import prama.ai.mapper.transformations.Transformation;
import prama.ai.mapper.transformations.ListValue;
import java.util.List;
import java.util.function.Predicate;

public class SingleOperandPredicate<T> implements Transformation<Boolean> {

    private Transformation<T> sourceTransformation;
    private Predicate<T> predicate;

    public SingleOperandPredicate() {

    }

    public SingleOperandPredicate(Transformation<T> sourceTransformation, Predicate<T> predicate) {

        this.sourceTransformation = sourceTransformation;
        this.predicate = predicate;
    }

    protected void setPredicate(Predicate<T> predicate) {

        this.predicate = predicate;
    }

    protected void setSourceTransformation(Transformation<T> sourceTransformation) {

        this.sourceTransformation = sourceTransformation;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Boolean apply(Object source) {

        Boolean result = true;
        if (sourceTransformation instanceof ListValue) {
            List<Transformation<T>> items = ((ListValue) sourceTransformation).getItems();
            for (int i = 0; i < items.size(); i++) {
                result = result && predicate.test(items.get(i).apply(source));
            }
        } else {
            T value = sourceTransformation.apply(source);
            result = predicate.test(value);
        }
        return result;
    }

    @Override
    public String toString() {

        return getClass().getSimpleName() + "(" + sourceTransformation + ")";
    }
}