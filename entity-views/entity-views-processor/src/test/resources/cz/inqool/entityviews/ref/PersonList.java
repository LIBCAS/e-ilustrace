package cz.inqool.entityviews.ref;

import javax.persistence.*;

import java.util.Set;

@javax.persistence.Entity
public class PersonList implements cz.inqool.entityviews.View {
    public String firstName;

    @javax.persistence.AttributeOverride(name = "id", column = @javax.persistence.Column(name = "address_id"))
    @javax.persistence.Embedded
    public cz.inqool.entityviews.ref.AddressRef address;

    @javax.persistence.AttributeOverride(name = "id", column = @javax.persistence.Column(name = "passport_id"))
    @javax.persistence.Embedded
    public cz.inqool.entityviews.ref.PassportRef passport;

    @javax.persistence.ElementCollection(fetch = javax.persistence.FetchType.EAGER)
    @javax.persistence.AttributeOverride(name = "id", column = @javax.persistence.Column(name = "name_id"))
    @javax.persistence.CollectionTable(name = "person_names", joinColumns = {@javax.persistence.JoinColumn(name = "person_id")})
    public java.util.Set<cz.inqool.entityviews.ref.NameRef> names;

    public static void toEntity(Person entity, PersonList view) {
        if (view == null) {
            return;
        }

        entity.firstName = view.firstName;
        entity.address = cz.inqool.entityviews.ref.AddressRef.toEntity(view.address);
        entity.passport = cz.inqool.entityviews.ref.PassportRef.toEntity(view.passport);
        entity.names = cz.inqool.entityviews.ref.NameRef.toEntities(view.names, java.util.LinkedHashSet::new);
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
        if (views == null) {
            return null;
        }

        return views.stream().map(view -> toEntity(view)).collect(java.util.stream.Collectors.toCollection(supplier));
    }

    public static void toView(PersonList view, Person entity) {
        if (entity == null) {
            return;
        }

        view.firstName = entity.firstName;
        view.address = cz.inqool.entityviews.ref.AddressRef.toRef(entity.address);
        view.passport = cz.inqool.entityviews.ref.PassportRef.toRef(entity.passport);
        view.names = cz.inqool.entityviews.ref.NameRef.toRefs(entity.names, java.util.LinkedHashSet::new);
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
        if (entities == null) {
            return null;
        }

        return entities.stream().map(entity -> toView(entity)).collect(java.util.stream.Collectors.toCollection(supplier));
    }
}
