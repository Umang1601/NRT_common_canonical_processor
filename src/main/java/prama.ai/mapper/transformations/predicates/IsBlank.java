package prama.ai.mapper.transformations.predicates;
import prama.ai.mapper.annotation.Transformer;
import prama.ai.mapper.transformations.Transformation;


@Transformer("isBlank")
public class IsBlank extends SingleOperandPredicate<Object> {

    public IsBlank(Transformation<Object> sourceTransformation) {

        super(sourceTransformation, t -> isBlank(t));
    }

    public static boolean isBlank(Object value) {

        if (value == null) {
            return true;
        } else if (value instanceof String) {
            return ((String) value).trim().isEmpty();
        }
        return false;
    }
}