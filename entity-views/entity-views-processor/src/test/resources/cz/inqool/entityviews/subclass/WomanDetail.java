package cz.inqool.entityviews.subclass;

import java.util.Set;

@javax.persistence.Entity()
@javax.persistence.DiscriminatorValue(value = "WOMAN")
public class WomanDetail extends cz.inqool.entityviews.subclass.PersonDetail implements cz.inqool.entityviews.View {
    public int kindness;


    @java.lang.Override()
    public String getType() {
        return "WOMAN";
    }

    @java.lang.Override()
    public java.util.Set<? extends cz.inqool.entityviews.subclass.PersonDetail> getSelfs() {
        return Set.of(this);
    }

    public static void toEntity(Woman entity, WomanDetail view) {
        toEntity(entity, view, true);
    }

    public static void toEntity(Woman entity, WomanDetail view, boolean callSuper) {
        if (view == null) {
            return;
        }
        if (callSuper) {
            cz.inqool.entityviews.subclass.PersonDetail.toEntity(entity, view, false);
        }
        entity.kindness = view.kindness;
    }

    public static void toEntity(cz.inqool.entityviews.subclass.Person entity, WomanDetail view) {
        toEntity((Woman) entity, view);
    }

    public static Woman toEntity(WomanDetail view) {
        if (view == null) {
            return null;
        }
        Woman entity = new Woman();
        toEntity(entity, view);
        return entity;
    }

    public static cz.inqool.entityviews.subclass.Person toEntity(cz.inqool.entityviews.subclass.PersonDetail view) {
        return toEntity((WomanDetail) view);
    }

    public static void toView(WomanDetail view, Woman entity) {
        toView(view, entity, true);
    }

    public static void toView(WomanDetail view, Woman entity, boolean callSuper) {
        if (entity == null) {
            return;
        }
        if (callSuper) {
            cz.inqool.entityviews.subclass.PersonDetail.toView(view, entity, false);
        }
        view.kindness = entity.kindness;
    }

    public static void toView(cz.inqool.entityviews.subclass.PersonDetail view, cz.inqool.entityviews.subclass.Person entity) {
        toView((WomanDetail) view, (Woman) entity);
    }

    public static WomanDetail toView(Woman entity) {
        if (entity == null) {
            return null;
        }
        WomanDetail view = new WomanDetail();
        toView(view, entity);
        return view;
    }

    public static cz.inqool.entityviews.subclass.PersonDetail toView(cz.inqool.entityviews.subclass.Person entity) {
        return toView((Woman) entity);
    }
}
