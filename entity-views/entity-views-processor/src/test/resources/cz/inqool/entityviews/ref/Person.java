package cz.inqool.entityviews.ref;

import javax.persistence.*;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;
import org.hibernate.annotations.Fetch;

import java.util.Set;

@ViewableClass(views = {"list"})
@Entity
public class Person {
    public String firstName;

    @ViewableProperty(views = {"list"})
    @ViewableMapping(views = "list", useRef = true)
    @ManyToOne
    @JoinColumn(name = "address_id")
    public Address address;

    @ViewableProperty(views = {"list"})
    @ViewableMapping(views = "list", useRef = true)
    @ManyToOne
    @JoinColumn(name = "passport_id")
    public Passport<String> passport;

    @ViewableProperty(views = {"list"})
    @ViewableMapping(views = "list", useRef = true)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "person_names", joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "name_id"))
    public Set<Name> names;
}
