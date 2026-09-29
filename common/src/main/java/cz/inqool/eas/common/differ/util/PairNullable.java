package cz.inqool.eas.common.differ.util;

import lombok.Getter;

@Getter
public class PairNullable<S, T> {

    private final S oldObject;
    private final T newObject;

    private PairNullable(S oldObject, T newObject) {
        this.oldObject = oldObject;
        this.newObject = newObject;
    }

    /**
     * Creates a new {@link PairNullable} for the given elements.
     *
     * @param oldObject must not be {@literal null}.
     * @param newObject must not be {@literal null}.
     */
    public static <S, T> PairNullable<S, T> of(S oldObject, T newObject) {
        return new PairNullable<>(oldObject, newObject);
    }
}