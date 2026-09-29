package cz.inqool.entityviews.context;

import cz.inqool.entityviews.ViewContext;

public class ContextHolder {
    private static ThreadLocal<Context> holder = new ThreadLocal<>();

    public static void setContext(Context context) {
        holder.set(context);
    }

    public static Context getContext() {
        return holder.get();
    }

    public static void indented(Runnable runnable) {
        Context context = getContext();
        int tabs = context.getTabs();

        context.setTabs(tabs + 1);
        runnable.run();
        context.setTabs(tabs);
    }

    public static void inView(ViewContext view, Runnable runnable) {
        if (view.shouldSkip()) {
            return;
        }

        Context context = getContext();
        ViewContext oldView = context.getView();

        context.setView(view);
        runnable.run();
        context.setView(oldView);
    }

    public static void clearContext() {
        holder.remove();
    }
}
