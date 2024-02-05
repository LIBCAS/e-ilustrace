package cz.inqool.entityviews.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.stream.Collectors;


@AllArgsConstructor
@Getter
@Setter
public class AnnotationArrayModel {
    private Object[] objects;

    @Override
    public String toString() {
        String concatenated = Arrays.
                stream(objects).
                map(Object::toString).
                collect(Collectors.joining(", "));

        return "{" + concatenated + "}";
    }
}
