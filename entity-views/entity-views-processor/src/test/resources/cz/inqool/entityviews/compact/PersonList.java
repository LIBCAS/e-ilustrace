package cz.inqool.entityviews.compact;

public class PersonList implements cz.inqool.entityviews.View {
    public boolean active;

    public String firstName;

    public static void toEntity(Person entity, PersonList view) {
        if (view == null) {
            return;
        }

        entity.active = view.active;
        entity.firstName = view.firstName;
    }

    public static Person toEntity(PersonList view) {
        if (view == null) {
            return null;
        }

        Person entity = new Person();
        toEntity(entity, view);

        return entity;
    }

    public static <EVCollection extends java.util.Collection<Person>> EVCollection toEntities(java.util.Collection<PersonList> views, java.util.function.Supplier<EVCollection> supplier) {
        return toEntities(views, supplier, Person.class);
    }

    public static <EV extends Person, EVCollection extends java.util.Collection<EV>> EVCollection toEntities(java.util.Collection<? extends PersonList> views, java.util.function.Supplier<EVCollection> supplier, Class<EV> entityClass) {
        if (views == null) {
            return null;
        }

        return views.stream().map(view -> entityClass.cast(toEntity(view))).collect(java.util.stream.Collectors.toCollection(supplier));
    }

    public static void toView(PersonList view, Person entity) {
        if (entity == null) {
            return;
        }

        view.active = entity.active;
        view.firstName = entity.firstName;
    }

    public static PersonList toView(Person entity) {
        if (entity == null) {
            return null;
        }

        PersonList view = new PersonList();
        toView(view, entity);

        return view;
    }

    public static <EVCollection extends java.util.Collection<PersonList>> EVCollection toViews(java.util.Collection<Person> entities, java.util.function.Supplier<EVCollection> supplier) {
        return toViews(entities, supplier, PersonList.class);
    }

    public static <EV extends PersonList, EVCollection extends java.util.Collection<EV>> EVCollection toViews(java.util.Collection<? extends Person> entities, java.util.function.Supplier<EVCollection> supplier, Class<EV> viewClass) {
        if (entities == null) {
            return null;
        }

        return entities.stream().map(entity -> viewClass.cast(toView(entity))).collect(java.util.stream.Collectors.toCollection(supplier));
    }
}