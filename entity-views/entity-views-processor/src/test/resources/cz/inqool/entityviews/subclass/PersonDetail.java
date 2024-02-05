package cz.inqool.entityviews.subclass;

import javax.persistence.InheritanceType;
import java.util.Set;

@javax.persistence.Entity()
@javax.persistence.Table(name = "person")
@javax.persistence.Inheritance(strategy = javax.persistence.InheritanceType.SINGLE_TABLE)
@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@com.fasterxml.jackson.annotation.JsonSubTypes(value = {@com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "MAN", value = cz.inqool.entityviews.subclass.ManDetail.class), @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "WOMAN", value = cz.inqool.entityviews.subclass.WomanDetail.class)})
@io.swagger.v3.oas.annotations.media.Schema(oneOf = {cz.inqool.entityviews.subclass.ManDetail.class, cz.inqool.entityviews.subclass.WomanDetail.class}, discriminatorProperty = "type", discriminatorMapping = {@io.swagger.v3.oas.annotations.media.DiscriminatorMapping(value = "MAN", schema = cz.inqool.entityviews.subclass.ManDetail.class), @io.swagger.v3.oas.annotations.media.DiscriminatorMapping(value = "WOMAN", schema = cz.inqool.entityviews.subclass.WomanDetail.class)})
public abstract class PersonDetail implements cz.inqool.entityviews.AbstractView {
    public String firstName;

    public String address;


    public abstract String getType();

    public abstract java.util.Set<? extends cz.inqool.entityviews.subclass.PersonDetail> getSelfs();

    public static void toEntity(Person entity, PersonDetail view) {
        toEntity(entity, view, true);
    }

    public static void toEntity(Person entity, PersonDetail view, boolean callSub) {
        if (view == null) {
            return;
        }
        entity.firstName = view.firstName;
        entity.address = view.address;

        if (callSub) {
            if (view instanceof cz.inqool.entityviews.subclass.ManDetail) {
                cz.inqool.entityviews.subclass.ManDetail.toEntity((Man) entity, (cz.inqool.entityviews.subclass.ManDetail) view, false);
                return;
            }
            if (view instanceof cz.inqool.entityviews.subclass.WomanDetail) {
                cz.inqool.entityviews.subclass.WomanDetail.toEntity((Woman) entity, (cz.inqool.entityviews.subclass.WomanDetail) view, false);
                return;
            }
            throw new IllegalArgumentException("Type '" + view.getClass() + "' not recognized.");
        }
    }

    public static Person toEntity(PersonDetail view) {
        if (view == null) {
            return null;
        }

        if (view instanceof cz.inqool.entityviews.subclass.ManDetail) {
            return cz.inqool.entityviews.subclass.ManDetail.toEntity((cz.inqool.entityviews.subclass.ManDetail) view);
        }
        if (view instanceof cz.inqool.entityviews.subclass.WomanDetail) {
            return cz.inqool.entityviews.subclass.WomanDetail.toEntity((cz.inqool.entityviews.subclass.WomanDetail) view);
        }
        throw new IllegalArgumentException("Type '" + view.getClass() + "' not recognized.");
    }

    public static <EVCollection extends java.util.Collection<Person>>EVCollection toEntities(java.util.Collection<PersonDetail> views, java.util.function.Supplier<EVCollection> supplier) {
        return toEntities(views, supplier, Person.class);
    }

    public static <EV extends Person, EVCollection extends java.util.Collection<EV>> EVCollection toEntities(java.util.Collection<? extends PersonDetail> views, java.util.function.Supplier<EVCollection> supplier, Class<EV> entityClass) {
        if (views == null) {
            return null;
        }

        return views.stream().map(view -> entityClass.cast(toEntity(view))).collect(java.util.stream.Collectors.toCollection(supplier));
    }

    public static void toView(PersonDetail view, Person entity) {
        toView(view, entity, true);
    }

    public static void toView(PersonDetail view, Person entity, boolean callSub) {
        if (entity == null) {
            return;
        }
        view.firstName = entity.firstName;
        view.address = entity.address;

        if (callSub) {
            if (entity instanceof Man) {
                cz.inqool.entityviews.subclass.ManDetail.toView((cz.inqool.entityviews.subclass.ManDetail) view, (Man) entity, false);
                return;
            }
            if (entity instanceof Woman) {
                cz.inqool.entityviews.subclass.WomanDetail.toView((cz.inqool.entityviews.subclass.WomanDetail) view, (Woman) entity, false);
                return;
            }
            throw new IllegalArgumentException("Type '" + entity.getClass() + "' not recognized.");
        }
    }

    public static PersonDetail toView(Person entity) {
        if (entity == null) {
            return null;
        }

        if (entity instanceof Man) {
            return cz.inqool.entityviews.subclass.ManDetail.toView((Man) entity);
        }
        if (entity instanceof Woman) {
            return cz.inqool.entityviews.subclass.WomanDetail.toView((Woman) entity);
        }
        throw new IllegalArgumentException("Type '" + entity.getClass() + "' not recognized.");
    }

    public static <EVCollection extends java.util.Collection<PersonDetail>>EVCollection toViews(java.util.Collection<Person> entities, java.util.function.Supplier<EVCollection> supplier) {
        return toViews(entities, supplier, PersonDetail.class);
    }

    public static <EV extends PersonDetail, EVCollection extends java.util.Collection<EV>> EVCollection toViews(java.util.Collection<? extends Person> entities, java.util.function.Supplier<EVCollection> supplier, Class<EV> viewClass) {
        if (entities == null) {
            return null;
        }

        return entities.stream().map(entity -> viewClass.cast(toView(entity))).collect(java.util.stream.Collectors.toCollection(supplier));
    }

}