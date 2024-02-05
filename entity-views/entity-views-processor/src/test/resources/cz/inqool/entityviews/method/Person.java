package cz.inqool.entityviews.method;

import javax.persistence.Entity;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableProperty;

import java.util.ArrayList;
import java.util.List;

@ViewableClass(views = {"list", "detail", "complex"})
@Entity
public class Person {
    public String firstName;

    @ViewableProperty(views = "detail")
    public String address;

    @ViewableProperty(views = "detail")
    public String getLastName() {
        return "Initial";
    }

    @ViewableProperty(views = "complex")
    public String getBio(Long num, java.lang.Long num2) {
        List<String> bio = new ArrayList<>();
        bio.add("Initial");
        bio.add(num.toString());
        bio.add(num2.toString());
        return bio.toString();
    }
}
