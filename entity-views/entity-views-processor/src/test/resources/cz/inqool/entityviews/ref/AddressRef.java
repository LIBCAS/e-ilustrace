package cz.inqool.entityviews.ref;

@javax.persistence.Embeddable
public class AddressRef {
    public String id;

    public static Address toEntity(AddressRef ref) {
        if (ref == null) {
            return null;
        }

        Address entity = new Address();
        entity.id = ref.id;

        return entity;
    }

    public static <EVCollection extends java.util.Collection<Address>> EVCollection toEntities(java.util.Collection<AddressRef> refs, java.util.function.Supplier<EVCollection> supplier) {
        if (refs == null) {
            return null;
        }

        return refs.stream().map(AddressRef::toEntity).collect(java.util.stream.Collectors.toCollection(supplier));
    }

    public static AddressRef toRef(Address entity) {
        if (entity == null) {
            return null;
        }

        AddressRef ref = new AddressRef();
        ref.id = entity.id;

        return ref;
    }

    public static <EVCollection extends java.util.Collection<AddressRef>> EVCollection toRefs(java.util.Collection<Address> entities, java.util.function.Supplier<EVCollection> supplier) {
        if (entities == null) {
            return null;
        }

        return entities.stream().map(AddressRef::toRef).collect(java.util.stream.Collectors.toCollection(supplier));
    }
}
