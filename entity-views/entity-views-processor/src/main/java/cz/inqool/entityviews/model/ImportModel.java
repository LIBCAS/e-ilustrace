package cz.inqool.entityviews.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static cz.inqool.entityviews.function.Printable.println;

@AllArgsConstructor
@Getter
@Setter
public class ImportModel {
    private String name;
    private boolean isStatic;
    private boolean isAnnotation;

    public void print() {
        if (isStatic) {
            println("import static ", name, ";");
        } else {
            println("import ", name, ";");
        }
    }
}
