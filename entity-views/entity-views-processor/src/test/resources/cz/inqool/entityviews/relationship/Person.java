package cz.inqool.entityviews.relationship;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;

@ViewableClass(views = {"list", "detail"})
@Entity
public class Person {
    public String firstName;

    @ViewableProperty(views = {"detail", "list"})
    @ViewableMapping(views = "list", mappedTo = "person_list")
    @ManyToOne
    @JoinColumn(name = "address_id")
    public Address address;
}
