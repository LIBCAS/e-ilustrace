package cz.inqool.entityviews.compact;

import javax.persistence.Entity;

import cz.inqool.entityviews.ViewableProperty;

@CompactAnnotation
@Entity
public class Person {
    public boolean active;

    public String firstName;

    @ViewableProperty(views = "detail")
    public String address;
}
