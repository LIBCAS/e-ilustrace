package cz.inqool.entityviews.embedded;

import javax.persistence.Embeddable;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableProperty;

@ViewableClass(views = {"person_list"})
@Embeddable
public class Address {
    public String country;
}
