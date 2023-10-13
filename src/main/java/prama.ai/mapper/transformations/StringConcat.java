package prama.ai.mapper.transformations;

import java.util.List;
import prama.ai.mapper.annotation.Transformer;
@Transformer("concat")
public class StringConcat<T> implements Transformation<String> {

    private Transformation<List<String>> sourceTransformation;

    public StringConcat(Transformation<List<String>> sourceTransformation) {

        this.sourceTransformation = sourceTransformation;
    }

    @Override
    public String apply(Object source) {

        List<String> srcValues = sourceTransformation.apply(source);
        StringBuilder result = new StringBuilder("");
        for (String s : srcValues) {
            result.append(s);
        }
        return result.toString();
    }

    @Override
    public String toString() {

        return "StrConcat" + sourceTransformation;
    }
}
