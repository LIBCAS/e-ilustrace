package cz.inqool.entityviews.geninh;

import java.io.Serializable;

@javax.persistence.Entity
public abstract class PersonDetail<T> implements cz.inqool.entityviews.AbstractView, java.io.Serializable {
    public String firstName;

    public String address;

    public static <T> void toEntity(Person<T> entity, PersonDetail<T> view) {
        if (view == null) {
            return;
        }

        entity.firstName = view.firstName;
        entity.address = view.address;
    }

    public static <T> void toView(PersonDetail<T> view, Person<T> entity) {
        if (entity == null) {
            return;
        }

        view.firstName = entity.firstName;
        view.address = entity.address;
    }
}
