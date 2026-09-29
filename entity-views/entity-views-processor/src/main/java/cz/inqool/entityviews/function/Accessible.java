package cz.inqool.entityviews.function;

import static cz.inqool.entityviews.function.Printable.print;
import static java.lang.String.join;

public interface Accessible {
    String[] getModifiers();

    default void printModifiers() {
        String[] modifiers = getModifiers();
        print(join(" ", modifiers));

        if (modifiers.length > 0) {
            print(" ");
        }
    }
}
