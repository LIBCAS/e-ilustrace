package cz.inqool.entityviews.relationship;

@javax.persistence.Entity
public class AddressList implements cz.inqool.entityviews.View {
    public String country;

    public static void toEntity(Address entity, AddressList view) {
        if (view == null) {
            return;
        }

        entity.country = view.country;
    }

    public static Address toEntity(AddressList view) {
        if (view == null) {
            return null;
        }

        Address entity = new Address();
        toEntity(entity, view);

        return entity;
    }

    public static <EVCollection extends java.util.Collection<Address>> EVCollection toEntities(java.util.Collection<AddressList> views, java.util.function.Supplier<EVCollection> supplier) {
        return toEntities(views, supplier, Address.class);
    }

    public static <EV extends Address, EVCollection extends java.util.Collection<EV>> EVCollection toEntities(java.util.Collection<? extends AddressList> views, java.util.function.Supplier<EVCollection> supplier, Class<EV> entityClass) {
        if (views == null) {
            return null;
        }

        return views.stream().map(view -> entityClass.cast(toEntity(view))).collect(java.util.stream.Collectors.toCollection(supplier));
    }

    public static void toView(AddressList view, Address entity) {
        if (entity == null) {
            return;
        }

        view.country = entity.country;
    }

    public static AddressList toView(Address entity) {
        if (entity == null) {
            return null;
        }

        AddressList view = new AddressList();
        toView(view, entity);

        return view;
    }

    public static <EVCollection extends java.util.Collection<AddressList>> EVCollection toViews(java.util.Collection<Address> entities, java.util.function.Supplier<EVCollection> supplier) {
        return toViews(entities, supplier, AddressList.class);
    }

    public static <EV extends AddressList, EVCollection extends java.util.Collection<EV>> EVCollection toViews(java.util.Collection<? extends Address> entities, java.util.function.Supplier<EVCollection> supplier, Class<EV> viewClass) {
        if (entities == null) {
            return null;
        }

        return entities.stream().map(entity -> viewClass.cast(toView(entity))).collect(java.util.stream.Collectors.toCollection(supplier));
    }
}
