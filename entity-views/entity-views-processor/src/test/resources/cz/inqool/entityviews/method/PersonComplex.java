package cz.inqool.entityviews.method;

import java.util.ArrayList;
import java.util.List;

@javax.persistence.Entity
public class PersonComplex implements cz.inqool.entityviews.View {
    public String firstName;

    public String getBio(Long num, Long num2) {
        List<String> bio = new ArrayList<>();
        bio.add("Initial");
        bio.add(num.toString());
        bio.add(num2.toString());
        return bio.toString();
    }

    public static void toEntity(Person entity, PersonComplex view) {
        if (view == null) {
            return;
        }

        entity.firstName = view.firstName;
    }

    public static Person toEntity(PersonComplex view) {
        if (view == null) {
            return null;
        }

        Person entity = new Person();
        toEntity(entity, view);

        return entity;
    }

    public static <EVCollection extends java.util.Collection<Person>> EVCollection toEntities(java.util.Collection<PersonComplex> views, java.util.function.Supplier<EVCollection> supplier) {
        return toEntities(views, supplier, Person.class);
    }

    public static <EV extends Person, EVCollection extends java.util.Collection<EV>> EVCollection toEntities(java.util.Collection<? extends PersonComplex> views, java.util.function.Supplier<EVCollection> supplier, Class<EV> entityClass) {
        if (views == null) {
            return null;
        }

        return views.stream().map(view -> entityClass.cast(toEntity(view))).collect(java.util.stream.Collectors.toCollection(supplier));
    }

    public static void toView(PersonComplex view, Person entity) {
        if (entity == null) {
            return;
        }

        view.firstName = entity.firstName;
    }

    public static PersonComplex toView(Person entity) {
        if (entity == null) {
            return null;
        }

        PersonComplex view = new PersonComplex();
        toView(view, entity);

        return view;
    }

    public static <EVCollection extends java.util.Collection<PersonComplex>> EVCollection toViews(java.util.Collection<Person> entities, java.util.function.Supplier<EVCollection> supplier) {
        return toViews(entities, supplier, PersonComplex.class);
    }

    public static <EV extends PersonComplex, EVCollection extends java.util.Collection<EV>> EVCollection toViews(java.util.Collection<? extends Person> entities, java.util.function.Supplier<EVCollection> supplier, Class<EV> viewClass) {
        if (entities == null) {
            return null;
        }

        return entities.stream().map(entity -> viewClass.cast(toView(entity))).collect(java.util.stream.Collectors.toCollection(supplier));
    }
}