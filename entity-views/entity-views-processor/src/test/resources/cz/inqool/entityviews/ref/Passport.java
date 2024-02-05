package cz.inqool.entityviews.ref;

import javax.persistence.Entity;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableProperty;

import java.io.Serializable;

@ViewableClass(generateRef = true)
@Entity
public class Passport<T extends Serializable> {
    public String id;

    public T number;
}
