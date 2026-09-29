package cz.inqool.entityviews.abs;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableMapping;

@ViewableClass(views = {"simple"})
@ViewableMapping(views = "simple", mappedTo = ViewableMapping.SKIP)
public class Cyborg extends Person {
}
