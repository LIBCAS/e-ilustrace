package cz.inqool.entityviews.assign;

import javax.persistence.Entity;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableProperty;

@ViewableClass(views = {"detail"})
@Entity
public class Person {
    public String firstName = "test";
}
