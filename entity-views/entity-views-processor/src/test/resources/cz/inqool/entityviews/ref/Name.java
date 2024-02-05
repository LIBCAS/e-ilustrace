package cz.inqool.entityviews.ref;

import javax.persistence.Entity;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableProperty;

@ViewableClass(generateRef = true)
@Entity
public class Name {
    public String id;

    public String text;
}
