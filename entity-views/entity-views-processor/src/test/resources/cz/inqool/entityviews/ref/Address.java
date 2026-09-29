package cz.inqool.entityviews.ref;

import javax.persistence.Entity;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableProperty;

@ViewableClass(generateRef = true)
@Entity
public class Address {
    public String id;

    public String country;

    @ViewableProperty(views = {"detail"})
    public String street;
}
