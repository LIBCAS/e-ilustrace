package cz.inqool.entityviews.ref;

@javax.persistence.Embeddable
public class NameRef {
    public String id;

    public static Name toEntity(NameRef ref) {
        if (ref == null) {
            return null;
        }

        Name entity = new Name();
        entity.id = ref.id;

        return entity;

    }

    public static <EVCollection extends java.util.Collection<Name>> EVCollection toEntities(java.util.Collection<NameRef> refs, java.util.function.Supplier<EVCollection> supplier) {
        if (refs == null) {
            return null;
        }

        return refs.stream().map(NameRef::toEntity).collect(java.util.stream.Collectors.toCollection(supplier));
    }

    public static NameRef toRef(Name entity) {
        if (entity == null) {
            return null;
        }

        NameRef ref = new NameRef();
        ref.id = entity.id;

        return ref;
    }

    public static <EVCollection extends java.util.Collection<NameRef>> EVCollection toRefs(java.util.Collection<Name> entities, java.util.function.Supplier<EVCollection> supplier) {
        if (entities == null) {
            return null;
        }

        return entities.stream().map(NameRef::toRef).collect(java.util.stream.Collectors.toCollection(supplier));
    }
}
