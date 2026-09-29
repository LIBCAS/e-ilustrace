package cz.inqool.entityviews.ref;

import java.io.Serializable;

@javax.persistence.Embeddable
public class PassportRef {
    public String id;

    public static <T extends java.io.Serializable> Passport<T> toEntity(PassportRef ref) {
        if (ref == null) {
            return null;
        }

        Passport<T> entity = new Passport<T>();
        entity.id = ref.id;

        return entity;
    }

    public static <T extends java.io.Serializable, EVCollection extends java.util.Collection<Passport<T>>> EVCollection toEntities(java.util.Collection<PassportRef> refs, java.util.function.Supplier<EVCollection> supplier) {
        if (refs == null) {
            return null;
        }

        return refs.stream().map(PassportRef::<T>toEntity).collect(java.util.stream.Collectors.toCollection(supplier));
    }

    public static <T extends java.io.Serializable> PassportRef toRef(Passport<T> entity) {
        if (entity == null) {
            return null;
        }

        PassportRef ref = new PassportRef();
        ref.id = entity.id;

        return ref;
    }

    public static <T extends java.io.Serializable, EVCollection extends java.util.Collection<PassportRef>> EVCollection toRefs(java.util.Collection<Passport<T>> entities, java.util.function.Supplier<EVCollection> supplier) {
        if (entities == null) {
            return null;
        }

        return entities.stream().map(PassportRef::<T>toRef).collect(java.util.stream.Collectors.toCollection(supplier));
    }
}
