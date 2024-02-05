package cz.inqool.entityviews.annotate;

@javax.persistence.Embeddable
public class AddressPersonList implements cz.inqool.entityviews.View {
    public String country;

    public static void toEntity(Address entity, AddressPersonList view) {
        if (view == null) {
            return;
        }
        entity.country = view.country;
    }

    public static Address toEntity(AddressPersonList view) {
        if (view == null) {
            return null;
        }
        Address entity = new Address();
        toEntity(entity, view);
        return entity;
    }

    public static <EVCollection extends java.util.Collection<Address>> EVCollection toEntities(java.util.Collection<AddressPersonList> views, java.util.function.Supplier<EVCollection> supplier) {
        return toEntities(views, supplier, Address.class);
    }

    public static <EV extends Address, EVCollection extends java.util.Collection<EV>> EVCollection toEntities(java.util.Collection<? extends AddressPersonList> views, java.util.function.Supplier<EVCollection> supplier, Class<EV> entityClass) {
        if (views == null) {
            return null;
        }

        return views.stream().map(view -> entityClass.cast(toEntity(view))).collect(java.util.stream.Collectors.toCollection(supplier));
    }

    public static void toView(AddressPersonList view, Address entity) {
        if (entity == null) {
            return;
        }
        view.country = entity.country;
    }

    public static AddressPersonList toView(Address entity) {
        if (entity == null) {
            return null;
        }
        AddressPersonList view = new AddressPersonList();
        toView(view, entity);
        return view;
    }

    public static <EVCollection extends java.util.Collection<AddressPersonList>> EVCollection toViews(java.util.Collection<Address> entities, java.util.function.Supplier<EVCollection> supplier) {
        return toViews(entities, supplier, AddressPersonList.class);
    }

    public static <EV extends AddressPersonList, EVCollection extends java.util.Collection<EV>> EVCollection toViews(java.util.Collection<? extends Address> entities, java.util.function.Supplier<EVCollection> supplier, Class<EV> viewClass) {
        if (entities == null) {
            return null;
        }

        return entities.stream().map(entity -> viewClass.cast(toView(entity))).collect(java.util.stream.Collectors.toCollection(supplier));
    }
}
