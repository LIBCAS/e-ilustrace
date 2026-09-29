package cz.inqool.entityviews.abs;

import java.io.Serializable;

@javax.persistence.Entity
public abstract class PersonDetail implements cz.inqool.entityviews.AbstractView, java.io.Serializable {
    public String firstName;

    public String address;

    public static void toEntity(Person entity, PersonDetail view) {
        if (view == null) {
            return;
        }

        entity.firstName = view.firstName;
        entity.address = view.address;
    }

    public static void toView(PersonDetail view, Person entity) {
        if (entity == null) {
            return;
        }

        view.firstName = entity.firstName;
        view.address = entity.address;
    }
}
