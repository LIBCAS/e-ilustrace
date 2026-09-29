package cz.inqool.entityviews.simple;

import javax.persistence.Entity;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableProperty;

@ViewableClass(views = {"list", "detail"})
@Entity
public class Person {
    public boolean active;

    public String firstName;

    @ViewableProperty(views = "detail")
    public String address;
}
