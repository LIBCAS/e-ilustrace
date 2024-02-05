package cz.inqool.entityviews.annotate;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.inqool.entityviews.ViewableAnnotation;
import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;

@ViewableClass(views = {"list", "detail", "create"})
@ViewableAnnotation(value = javax.persistence.Entity.class, views = {"list", "detail"})
@Entity
public class Person {
    public boolean active;

    @ViewableProperty(views = {"list", "detail", "create"})
    @Column(name = "first_name")
    @ViewableAnnotation(views = {"list", "detail"}, value = Column.class)
    public String firstName;

    @ViewableProperty(views = {"list", "detail", "create"})
    @ViewableMapping(views = "list", mappedTo = "person_list")
    @ViewableMapping(views = "detail", mappedTo = "person_detail")
    @ViewableMapping(views = "create", mappedTo = "person_create")
    @ViewableAnnotation(views = {"list", "detail"}, value = Embedded.class)
    @Embedded
    public Address address;
}
