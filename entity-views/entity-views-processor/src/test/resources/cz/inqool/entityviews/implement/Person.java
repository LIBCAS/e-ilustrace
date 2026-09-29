package cz.inqool.entityviews.implement;

import javax.persistence.Entity;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableImplement;
import cz.inqool.entityviews.ViewableProperty;

@ViewableClass(views = {"list", "detail"})
@ViewableImplement(value = Humanoid.class, views = "detail")
@Entity
public class Person implements Humanoid<Person> {
    public boolean active;

    public String firstName;

    @ViewableProperty(views = "detail")
    public String address;
}
