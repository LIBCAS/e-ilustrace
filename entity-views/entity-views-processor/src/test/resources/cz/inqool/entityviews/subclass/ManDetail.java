package cz.inqool.entityviews.subclass;

import org.hibernate.annotations.FetchMode;
import java.util.Set;

@javax.persistence.Entity()
@javax.persistence.DiscriminatorValue(value = "MAN")
public class ManDetail extends cz.inqool.entityviews.subclass.PersonDetail implements cz.inqool.entityviews.View {
    public int strength;

    public int iq;

    @org.hibernate.annotations.Fetch(value = org.hibernate.annotations.FetchMode.SELECT)
    @javax.persistence.ManyToOne(targetEntity = cz.inqool.entityviews.subclass.PersonDetail.class)
    @javax.persistence.JoinColumn(name = "wife_id")
    public cz.inqool.entityviews.subclass.WomanDetail wife;


    @java.lang.Override()
    public String getType() {
        return "MAN";
    }

    @java.lang.Override()
    public java.util.Set<? extends cz.inqool.entityviews.subclass.PersonDetail> getSelfs() {
        return Set.of(this);
    }

    public static void toEntity(Man entity, ManDetail view) {
        toEntity(entity, view, true);
    }

    public static void toEntity(Man entity, ManDetail view, boolean callSuper) {
        if (view == null) {
            return;
        }
        if (callSuper) {
            cz.inqool.entityviews.subclass.PersonDetail.toEntity(entity, view, false);
        }
        entity.strength = view.strength;
        entity.iq = view.iq;
        entity.wife = cz.inqool.entityviews.subclass.WomanDetail.toEntity(view.wife);
    }

    public static void toEntity(cz.inqool.entityviews.subclass.Person entity, ManDetail view) {
        toEntity((Man) entity, view);
    }

    public static Man toEntity(ManDetail view) {
        if (view == null) {
            return null;
        }
        Man entity = new Man();
        toEntity(entity, view);
        return entity;
    }

    public static cz.inqool.entityviews.subclass.Person toEntity(cz.inqool.entityviews.subclass.PersonDetail view) {
        return toEntity((ManDetail) view);
    }

    public static void toView(ManDetail view, Man entity) {
        toView(view, entity, true);
    }

    public static void toView(ManDetail view, Man entity, boolean callSuper) {
        if (entity == null) {
            return;
        }
        if (callSuper) {
            cz.inqool.entityviews.subclass.PersonDetail.toView(view, entity, false);
        }
        view.strength = entity.strength;
        view.iq = entity.iq;
        view.wife = cz.inqool.entityviews.subclass.WomanDetail.toView(entity.wife);
    }

    public static void toView(cz.inqool.entityviews.subclass.PersonDetail view, cz.inqool.entityviews.subclass.Person entity) {
        toView((ManDetail) view, (Man) entity);
    }

    public static ManDetail toView(Man entity) {
        if (entity == null) {
            return null;
        }
        ManDetail view = new ManDetail();
        toView(view, entity);
        return view;
    }

    public static cz.inqool.entityviews.subclass.PersonDetail toView(cz.inqool.entityviews.subclass.Person entity) {
        return toView((Man) entity);
    }

}