package prama.ai.mapper.transformations;
import prama.ai.mapper.annotation.Transformer;

@Transformer("policyReplace")
public class PolicyStringReplace implements Transformation<String> {

    private Transformation<String> sourceTransformation;

    public PolicyStringReplace(Transformation<String> sourceTransformation) {

        this.sourceTransformation = sourceTransformation;
    }

    @Override
    public String apply(Object source) {

        String srcValue = sourceTransformation.apply(source);
        String record= srcValue.replaceAll("\\p{C}","").replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "").replaceAll("[^\\x00-\\x7F]gu", "");;
        return record.toUpperCase().trim();
    }

    @Override
    public String toString() {

        return "PolicyReplace" + sourceTransformation;
    }
}