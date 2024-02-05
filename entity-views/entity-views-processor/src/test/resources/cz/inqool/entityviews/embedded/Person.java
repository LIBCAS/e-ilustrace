package cz.inqool.entityviews.embedded;

import javax.persistence.Entity;
import javax.persistence.Embedded;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;

@ViewableClass(views = {"list"})
@Entity
public class Person {
    public String firstName;

    @ViewableProperty(views = {"list"})
    @ViewableMapping(views = "list", mappedTo = "person_list")
    @Embedded
    public Address address;
}
