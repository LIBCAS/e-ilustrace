package cz.inqool.entityviews.relationship;

import javax.persistence.Entity;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableProperty;

@ViewableClass(views = {"list", "detail", "person_list"})
@Entity
public class Address {
    public String country;

    @ViewableProperty(views = {"detail"})
    public String street;
}
