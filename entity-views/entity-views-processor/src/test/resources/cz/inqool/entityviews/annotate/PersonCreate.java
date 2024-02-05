package cz.inqool.entityviews.annotate;

public class PersonCreate implements cz.inqool.entityviews.View {
    public boolean active;

    public String firstName;

    public cz.inqool.entityviews.annotate.AddressPersonCreate address;

    public static void toEntity(Person entity, PersonCreate view) {
        if (view == null) {
            return;
        }

        entity.active = view.active;
        entity.firstName = view.firstName;
        entity.address = cz.inqool.entityviews.annotate.AddressPersonCreate.toEntity(view.address);
    }

    public static Person toEntity(PersonCreate view) {
        if (view == null) {
            return null;
        }

        Person entity = new Person();
        toEntity(entity, view);

        return entity;
    }

    public static <EVCollection extends java.util.Collection<Person>> EVCollection toEntities(java.util.Collection<PersonCreate> views, java.util.function.Supplier<EVCollection> supplier) {
        return toEntities(views, supplier, Person.class);
    }

    public static <EV extends Person, EVCollection extends java.util.Collection<EV>> EVCollection toEntities(java.util.Collection<? extends PersonCreate> views, java.util.function.Supplier<EVCollection> supplier, Class<EV> entityClass) {
        if (views == null) {
            return null;
        }

        return views.stream().map(view -> entityClass.cast(toEntity(view))).collect(java.util.stream.Collectors.toCollection(supplier));
    }

    public static void toView(PersonCreate view, Person entity) {
        if (entity == null) {
            return;
        }

        view.active = entity.active;
        view.firstName = entity.firstName;
        view.address = cz.inqool.entityviews.annotate.AddressPersonCreate.toView(entity.address);
    }

    public static PersonCreate toView(Person entity) {
        if (entity == null) {
            return null;
        }

        PersonCreate view = new PersonCreate();
        toView(view, entity);

        return view;
    }

    public static <EVCollection extends java.util.Collection<PersonCreate>> EVCollection toViews(java.util.Collection<Person> entities, java.util.function.Supplier<EVCollection> supplier) {
        return toViews(entities, supplier, PersonCreate.class);
    }

    public static <EV extends PersonCreate, EVCollection extends java.util.Collection<EV>> EVCollection toViews(java.util.Collection<? extends Person> entities, java.util.function.Supplier<EVCollection> supplier, Class<EV> viewClass) {
        if (entities == null) {
            return null;
        }

        return entities.stream().map(entity -> viewClass.cast(toView(entity))).collect(java.util.stream.Collectors.toCollection(supplier));
    }
}