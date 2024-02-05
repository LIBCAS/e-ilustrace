package cz.inqool.entityviews.implement;

@javax.persistence.Entity
public class PersonDetail implements cz.inqool.entityviews.View, cz.inqool.entityviews.implement.Humanoid<PersonDetail> {
    public boolean active;

    public String firstName;

    public String address;

    public static void toEntity(Person entity, PersonDetail view) {
        if (view == null) {
            return;
        }

        entity.active = view.active;
        entity.firstName = view.firstName;
        entity.address = view.address;
    }

    public static Person toEntity(PersonDetail view) {
        if (view == null) {
            return null;
        }

        Person entity = new Person();
        toEntity(entity, view);

        return entity;
    }

    public static <EVCollection extends java.util.Collection<Person>> EVCollection toEntities(java.util.Collection<PersonDetail> views, java.util.function.Supplier<EVCollection> supplier) {
        return toEntities(views, supplier, Person.class);
    }

    public static <EV extends Person, EVCollection extends java.util.Collection<EV>> EVCollection toEntities(java.util.Collection<? extends PersonDetail> views, java.util.function.Supplier<EVCollection> supplier, Class<EV> entityClass) {
        if (views == null) {
            return null;
        }

        return views.stream().map(view -> entityClass.cast(toEntity(view))).collect(java.util.stream.Collectors.toCollection(supplier));
    }

    public static void toView(PersonDetail view, Person entity) {
        if (entity == null) {
            return;
        }

        view.active = entity.active;
        view.firstName = entity.firstName;
        view.address = entity.address;
    }

    public static PersonDetail toView(Person entity) {
        if (entity == null) {
            return null;
        }

        PersonDetail view = new PersonDetail();
        toView(view, entity);

        return view;
    }

    public static <EVCollection extends java.util.Collection<PersonDetail>> EVCollection toViews(java.util.Collection<Person> entities, java.util.function.Supplier<EVCollection> supplier) {
        return toViews(entities, supplier, PersonDetail.class);
    }

    public static <EV extends PersonDetail, EVCollection extends java.util.Collection<EV>> EVCollection toViews(java.util.Collection<? extends Person> entities, java.util.function.Supplier<EVCollection> supplier, Class<EV> viewClass) {
        if (entities == null) {
            return null;
        }

        return entities.stream().map(entity -> viewClass.cast(toView(entity))).collect(java.util.stream.Collectors.toCollection(supplier));
    }
}