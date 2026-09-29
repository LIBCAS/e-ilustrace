package cz.inqool.entityviews.geninh;

import javax.persistence.Entity;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableMapping;

@ViewableClass(views = {"detail", "simple"})
@ViewableMapping(views = "detail", mappedTo = "detail")
@Entity
public class Man extends Person<String> {
    public int strength;
}
