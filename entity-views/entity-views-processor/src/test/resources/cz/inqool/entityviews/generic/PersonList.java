package cz.inqool.entityviews.generic;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@javax.persistence.Entity
public class PersonList<T extends java.io.Serializable, U extends java.util.Collection<T>> implements cz.inqool.entityviews.View {
    public T firstName;

    public java.util.List<U> passports;

    public static <T extends java.io.Serializable, U extends java.util.Collection<T>> void toEntity(Person<T, U> entity, PersonList<T, U> view) {
        if (view == null) {
            return;
        }

        entity.firstName = view.firstName;
        entity.passports = view.passports;
    }

    public static <T extends java.io.Serializable, U extends java.util.Collection<T>> Person<T, U> toEntity(PersonList<T, U> view) {
        if (view == null) {
            return null;
        }

        Person<T, U> entity = new Person<T, U>();
        toEntity(entity, view);

        return entity;
    }

    public static <T extends java.io.Serializable, U extends java.util.Collection<T>, EVCollection extends java.util.Collection<Person<T, U>>> EVCollection toEntities(java.util.Collection<PersonList<T, U>> views, java.util.function.Supplier<EVCollection> supplier) {
        if (views == null) {
            return null;
        }

        return views.stream().map(view -> toEntity(view)).collect(java.util.stream.Collectors.toCollection(supplier));
    }

    public static  <T extends java.io.Serializable, U extends java.util.Collection<T>> void toView(PersonList<T, U> view, Person<T, U> entity) {
        if (entity == null) {
            return;
        }

        view.firstName = entity.firstName;
        view.passports = entity.passports;
    }

    public static <T extends java.io.Serializable, U extends java.util.Collection<T>> PersonList<T, U> toView(Person<T, U> entity) {
        if (entity == null) {
            return null;
        }

        PersonList<T, U> view = new PersonList<T, U>();
        toView(view, entity);

        return view;
    }

    public static <T extends java.io.Serializable, U extends java.util.Collection<T>, EVCollection extends java.util.Collection<PersonList<T, U>>> EVCollection toViews(java.util.Collection<Person<T, U>> entities, java.util.function.Supplier<EVCollection> supplier) {
        if (entities == null) {
            return null;
        }

        return entities.stream().map(entity -> toView(entity)).collect(java.util.stream.Collectors.toCollection(supplier));
    }
}