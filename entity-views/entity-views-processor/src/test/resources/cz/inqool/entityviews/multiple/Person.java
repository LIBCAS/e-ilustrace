package cz.inqool.entityviews.multiple;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;

import java.util.List;

@ViewableClass(views = {"list"})
@Entity
public class Person {
    @ViewableProperty(views = {"list"})
    @ViewableMapping(views = "list", mappedTo = "person_list")
    @OneToMany(mappedBy = "person", fetch = FetchType.EAGER)
    public List<Address> addresses;

    @ViewableProperty(views = {"list"})
    @ViewableMapping(views = "list", mappedTo = "person_list", useOneWay = true)
    @OneToMany(mappedBy = "person", fetch = FetchType.EAGER)
    public List<? extends Passport> passports;
}
