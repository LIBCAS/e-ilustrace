package cz.inqool.entityviews.subclass;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Set;

@ViewableClass(views = {"detail"})
@ViewableMapping(views = {"detail"}, mappedTo = "detail")
@Entity
@DiscriminatorValue("WOMAN")
public class Woman extends Person {

    public int kindness;

    @Override
    public String getType() {
        return "WOMAN";
    }

    @Override
    @ViewableProperty(views = "detail")
    @ViewableMapping(views = "detail", mappedTo = "detail")
    public Set<? extends Person> getSelfs() {
        return Set.of(this);
    }
}
