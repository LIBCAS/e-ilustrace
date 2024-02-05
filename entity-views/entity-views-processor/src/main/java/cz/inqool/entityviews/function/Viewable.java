package cz.inqool.entityviews.function;

import cz.inqool.entityviews.ViewContext;

import java.util.Map;

import static cz.inqool.entityviews.context.ContextHolder.getContext;
import static java.util.Arrays.asList;

public interface Viewable {
    String[] getViews();
    Map<String, ViewContext> getViewMappings();

    default boolean skipInView() {
        ViewContext view = getContext().getView();
        return skipInView(view);
    }

    default boolean skipInView(ViewContext view) {
        String[] views = getViews();
        return views != null && !asList(views).contains(view.getName());
    }

    default boolean includeInView() {
        return !skipInView();
    }

    default boolean includeInView(ViewContext view) {
        return !skipInView(view);
    }

    default ViewContext getMappedView() {
        ViewContext view = getContext().getView();
        String[] views = getViews();
        Map<String, ViewContext> viewMappings = getViewMappings();

        if (views != null) {
            if (viewMappings != null) {
                ViewContext mappedView = viewMappings.get(view.getName());

                if (mappedView != null) {
                    return mappedView;
                }
            }
        }

        return ViewContext.EMPTY_VIEW;
    }
}
