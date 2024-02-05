package cz.inqool.entityviews.geninh;

import javax.persistence.Entity;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableProperty;

import java.io.Serializable;

@ViewableClass(views = {"detail"})
@Entity
public abstract class Person<T> implements Serializable {
    public String firstName;

    @ViewableProperty(views = "detail")
    public String address;
}
