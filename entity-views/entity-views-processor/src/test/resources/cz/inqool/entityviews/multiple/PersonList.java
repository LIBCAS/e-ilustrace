package cz.inqool.entityviews.multiple;

import javax.persistence.FetchType;
import java.util.List;

@javax.persistence.Entity
public class PersonList implements cz.inqool.entityviews.View {
    @javax.persistence.OneToMany(mappedBy = "person", fetch = javax.persistence.FetchType.EAGER)
    public java.util.List<cz.inqool.entityviews.multiple.AddressPersonList> addresses;

    @javax.persistence.OneToMany(fetch = javax.persistence.FetchType.EAGER )
    @javax.persistence.JoinColumn(name = "person_id")
    public java.util.List<? extends cz.inqool.entityviews.multiple.PassportPersonList> passports;

    public static void toEntity(Person entity, PersonList view) {
        if (view == null) {
            return;
        }

        entity.addresses = cz.inqool.entityviews.multiple.AddressPersonList.toEntities(view.addresses, java.util.ArrayList::new, cz.inqool.entityviews.multiple.Address.class);
        entity.passports = cz.inqool.entityviews.multiple.PassportPersonList.toEntities(view.passports, java.util.ArrayList::new, cz.inqool.entityviews.multiple.Passport.class);
        if (entity.passports != null) entity.passports.stream().filter(o->o != null).forEach(o->o.setPerson(entity));
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

        view.addresses = cz.inqool.entityviews.multiple.AddressPersonList.toViews(entity.addresses, java.util.ArrayList::new, cz.inqool.entityviews.multiple.AddressPersonList.class);
        view.passports = cz.inqool.entityviews.multiple.PassportPersonList.toViews(entity.passports, java.util.ArrayList::new, cz.inqool.entityviews.multiple.PassportPersonList.class);
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
