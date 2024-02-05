package cz.inqool.entityviews.implement;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableImplement;

@ViewableClass(views = {"list"})
@ViewableImplement(value = Humanoid.class, views = {})
public class Cyborg implements Humanoid<Cyborg> {
}
