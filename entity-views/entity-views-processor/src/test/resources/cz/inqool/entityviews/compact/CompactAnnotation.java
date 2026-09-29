package cz.inqool.entityviews.compact;


import cz.inqool.entityviews.ViewableAnnotation;
import cz.inqool.entityviews.ViewableClass;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@ViewableClass(views = {"list", "detail"})
@ViewableAnnotation(value = javax.persistence.Entity.class, views = "detail")
public @interface CompactAnnotation {
}
