package prama.ai.mapper.transformations;

import prama.ai.mapper.annotation.Transformer;

import java.util.ArrayList;
import java.util.List;


@Transformer("list")
public class ListValue<T> implements Transformation<List<T>> {

    private List<Transformation<T>> items = new ArrayList<>();

    public void add(Transformation<T> itemTransformation) {

        this.items.add(itemTransformation);
    }

    @Override
    public List<T> apply(Object source) {

        ArrayList<T> result = new ArrayList<>();
        for (Transformation<T> item : items) {
            result.add(item.apply(source));
        }
        return result;
    }

    public List<Transformation<T>> getItems() {

        return items;
    }

    @Override
    public String toString() {

        return items.toString();
    }
}
