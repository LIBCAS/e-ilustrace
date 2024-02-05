package cz.inqool.entityviews.generic;

import javax.persistence.Entity;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableProperty;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@ViewableClass(views = {"list"})
@Entity
public class Person<T extends Serializable, U extends Collection<T>> {
    public T firstName;

    public List<U> passports;
}
