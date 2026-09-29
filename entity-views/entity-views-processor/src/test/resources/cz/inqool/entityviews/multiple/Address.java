package cz.inqool.entityviews.multiple;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import cz.inqool.entityviews.ViewableMapping;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableProperty;

@ViewableClass(views = {"person_list"})
@Entity
public class Address {
    @ViewableProperty(views = {"person_list"})
    @ViewableMapping(views = "person_list", mappedTo = "list")
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    @JoinColumn(name = "person_id")
    public Person person;
}
