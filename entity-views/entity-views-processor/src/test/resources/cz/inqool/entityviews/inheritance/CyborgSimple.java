package cz.inqool.entityviews.abs;

public class CyborgSimple implements cz.inqool.entityviews.View {
    public static void toEntity(Cyborg entity, CyborgSimple view) {
        if (view == null) {
            return;
        }
    }

    public static Cyborg toEntity(CyborgSimple view) {
        if (view == null) {
            return null;
        }

        Cyborg entity = new Cyborg();
        toEntity(entity, view);

        return entity;
    }

    public static <EVCollection extends java.util.Collection<Cyborg>> EVCollection toEntities(java.util.Collection<CyborgSimple> views, java.util.function.Supplier<EVCollection> supplier) {
        return toEntities(views, supplier, Cyborg.class);
    }

    public static <EV extends Cyborg, EVCollection extends java.util.Collection<EV>> EVCollection toEntities(java.util.Collection<? extends CyborgSimple> views, java.util.function.Supplier<EVCollection> supplier, Class<EV> entityClass) {
        if (views == null) {
            return null;
        }

        return views.stream().map(view -> entityClass.cast(toEntity(view))).collect(java.util.stream.Collectors.toCollection(supplier));
    }

    public static void toView(CyborgSimple view, Cyborg entity) {
        if (entity == null) {
            return;
        }
    }

    public static CyborgSimple toView(Cyborg entity) {
        if (entity == null) {
            return null;
        }

        CyborgSimple view = new CyborgSimple();
        toView(view, entity);

        return view;
    }

    public static <EVCollection extends java.util.Collection<CyborgSimple>> EVCollection toViews(java.util.Collection<Cyborg> entities, java.util.function.Supplier<EVCollection> supplier) {
        return toViews(entities, supplier, CyborgSimple.class);
    }

    public static <EV extends CyborgSimple, EVCollection extends java.util.Collection<EV>> EVCollection toViews(java.util.Collection<? extends Cyborg> entities, java.util.function.Supplier<EVCollection> supplier, Class<EV> viewClass) {
        if (entities == null) {
            return null;
        }

        return entities.stream().map(entity -> viewClass.cast(toView(entity))).collect(java.util.stream.Collectors.toCollection(supplier));
    }
}
