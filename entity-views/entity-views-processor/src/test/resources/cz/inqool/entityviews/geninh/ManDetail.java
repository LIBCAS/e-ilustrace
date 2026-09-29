package cz.inqool.entityviews.geninh;

@javax.persistence.Entity
public class ManDetail extends cz.inqool.entityviews.geninh.PersonDetail<String> implements cz.inqool.entityviews.View {
    public int strength;

    public static void toEntity(Man entity, ManDetail view) {
        if (view == null) {
            return;
        }

        cz.inqool.entityviews.geninh.PersonDetail.toEntity(entity, view);

        entity.strength = view.strength;
    }

    public static Man toEntity(ManDetail view) {
        if (view == null) {
            return null;
        }

        Man entity = new Man();
        toEntity(entity, view);

        return entity;
    }

    public static <EVCollection extends java.util.Collection<Man>> EVCollection toEntities(java.util.Collection<ManDetail> views, java.util.function.Supplier<EVCollection> supplier) {
        return toEntities(views, supplier, Man.class);
    }

    public static <EV extends Man, EVCollection extends java.util.Collection<EV>> EVCollection toEntities(java.util.Collection<? extends ManDetail> views, java.util.function.Supplier<EVCollection> supplier, Class<EV> entityClass) {
        if (views == null) {
            return null;
        }

        return views.stream().map(view -> entityClass.cast(toEntity(view))).collect(java.util.stream.Collectors.toCollection(supplier));
    }

    public static void toView(ManDetail view, Man entity) {
        if (entity == null) {
            return;
        }

        cz.inqool.entityviews.geninh.PersonDetail.toView(view, entity);

        view.strength = entity.strength;
    }

    public static ManDetail toView(Man entity) {
        if (entity == null) {
            return null;
        }

        ManDetail view = new ManDetail();
        toView(view, entity);

        return view;
    }

    public static <EVCollection extends java.util.Collection<ManDetail>> EVCollection toViews(java.util.Collection<Man> entities, java.util.function.Supplier<EVCollection> supplier) {
        return toViews(entities, supplier, ManDetail.class);
    }

    public static <EV extends ManDetail, EVCollection extends java.util.Collection<EV>> EVCollection toViews(java.util.Collection<? extends Man> entities, java.util.function.Supplier<EVCollection> supplier, Class<EV> viewClass) {
        if (entities == null) {
            return null;
        }

        return entities.stream().map(entity -> viewClass.cast(toView(entity))).collect(java.util.stream.Collectors.toCollection(supplier));
    }
}
