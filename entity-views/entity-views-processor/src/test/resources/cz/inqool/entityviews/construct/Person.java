package cz.inqool.entityviews.construct;

import javax.persistence.Entity;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableProperty;

@ViewableClass(views = {"list"})
@Entity
public class Person {
    public boolean active;

    public String firstName;

    public Person() {
        this.active = false;
        this.firstName = "none";
    }

    protected Person(String firstName) {
        this.active = false;
        this.firstName = firstName;
    }
}
