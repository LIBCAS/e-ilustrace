package cz.inqool.entityviews.subclass;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Set;

@ViewableClass(views = {"detail"})
@ViewableMapping(views = {"detail"}, mappedTo = "detail")
@Entity
@DiscriminatorValue("MAN")
public class Man extends Person {

    public int strength;

    @ViewableProperty(views = "detail")
    public int iq;

    @ViewableProperty(views = "detail")
    @ViewableMapping(views = "detail", mappedTo = "detail")
    @Fetch(FetchMode.SELECT)
    @ManyToOne(targetEntity = Person.class)
    @JoinColumn(name = "wife_id")
    public Woman wife;

    @Override
    public String getType() {
        return "MAN";
    }

    @Override
    @ViewableProperty(views = "detail")
    @ViewableMapping(views = "detail", mappedTo = "detail")
    public Set<? extends Person> getSelfs() {
        return Set.of(this);
    }
}
