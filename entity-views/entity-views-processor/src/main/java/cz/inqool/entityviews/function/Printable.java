package cz.inqool.entityviews.function;

import java.io.PrintWriter;

import static cz.inqool.entityviews.context.ContextHolder.getContext;

public class Printable {

    public static void println(String... texts) {
        startLine();

        print(texts);

        endLine();
    }

    public static void println(Runnable runnable) {
        startLine();

        runnable.run();

        endLine();
    }

    public static void println() {
        PrintWriter out = getContext().getOut();
        out.println();
    }

    public static void print(String... texts) {
        PrintWriter out = getContext().getOut();
        for (String text : texts) {
            out.print(text);
        }
    }

    private static void startLine() {
        int tabs = getContext().getTabs();
        PrintWriter out = getContext().getOut();
        for (int i = 0; i < tabs; i++) {
            out.print("\t");
        }
    }

    private static void endLine() {
        PrintWriter out = getContext().getOut();
        out.println();
    }

    public static String addIndent(String src) {
        int tabs = getContext().getTabs();
        return src.replaceAll("\n", "\n" + "\t".repeat(tabs));
    }
}
