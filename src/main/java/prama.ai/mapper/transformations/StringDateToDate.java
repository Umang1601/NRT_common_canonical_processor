package prama.ai.mapper.transformations;

import prama.ai.mapper.annotation.Transformer;
import prama.ai.mapper.transformations.predicates.IsBlank;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

//import com.unum.mapper.annotation.Transformer;
//import com.unum.mapper.model.MapperException;
//import com.unum.mapper.model.MappingModel;
//import com.unum.mapper.transformations.predicates.IsBlank;

@Transformer("date")
public class StringDateToDate implements Transformation<Date> {

    private String formatStr;
    private boolean formatContainsTimeZone;
    protected Transformation<String> sourceTransformation;

    public StringDateToDate(Transformation<?> sourceTransformation) {

        this(sourceTransformation, "yyyyMMdd");
    }

    @SuppressWarnings("unchecked")
    public StringDateToDate(Transformation<?> srcTx, String formatStr) {

        if (srcTx instanceof MapValue) {
            Object dummy = new Object();
            Map<String, Transformation<String>> intermediateMap = ((MapValue<String, String>) srcTx).getIntermediateMap(dummy);
            formatStr = intermediateMap.get("format").apply(dummy);
            this.sourceTransformation = intermediateMap.get("value");
        } else {
            this.sourceTransformation = (Transformation<String>) srcTx;
        }

        this.formatStr = formatStr;

        formatContainsTimeZone = formatStr.contains("Z") || formatStr.contains("XXX");
    }

    @Override
    public Date apply(Object source) {

        String value = sourceTransformation.apply(source);

        return IsBlank.isBlank(value) ? null : parse(value);
    }

    protected Date parse(String value) {

        DateFormat format = getDateFormat();
        try {
            if (formatContainsTimeZone) {
                return format.parse(value);
            } else {
                String trimmed = getTrimmed(value);
                return format.parse(trimmed + " +0000");
            }
        } catch (ParseException e) {
            System.out.println("Exception received while parsing date : ");
            // exception is not yet implemented
            //MappingModel.recordException(new MapperException("Failed to parse " + value + " expected format is: " + formatStr, e));
        }
        return null;
    }

    private DateFormat getDateFormat() {

        DateFormat format;
        if (formatContainsTimeZone) {
            format = new SimpleDateFormat(formatStr);
        } else {
            format = new SimpleDateFormat(formatStr + " Z");
        }
        return format;
    }

    private String getTrimmed(String value) {

        String trimmed = value;
        String[] parts = value.split("\\.");
        if (parts.length > 1 && parts[1].length() > 3) {
            trimmed = parts[0] + "." + parts[1].substring(0, 3);
        }
        return trimmed;
    }

    @Override
    public String toString() {

        return "StrToDate[" + sourceTransformation + "#" + formatStr + "]";
    }
}