package prama.ai.mapper.transformations;

import prama.ai.mapper.annotation.Transformer;

@Transformer("trim")
public class StringTrim<T> implements Transformation<String> {

    private Transformation<String> sourceTransformation;

    public StringTrim(Transformation<String> sourceTransformation) {

        this.sourceTransformation = sourceTransformation;
    }

    @Override
    public String apply(Object source) {

        String value = sourceTransformation.apply(source);
        return value == null ? null : ((String) value).trim();
    }

    @Override
    public String toString() {

        return "Trim[" + sourceTransformation + "]";
    }
}
