package cz.inqool.entityviews.model;

import cz.inqool.entityviews.ViewContext;
import cz.inqool.entityviews.function.Accessible;
import cz.inqool.entityviews.function.Printable;
import cz.inqool.entityviews.function.Viewable;
import cz.inqool.entityviews.model.type.TypeModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.inqool.entityviews.context.ContextHolder.inView;
import static cz.inqool.entityviews.function.Printable.*;

@AllArgsConstructor
@Getter
@Setter
public class MethodModel implements Viewable, Accessible {
    private String name;
    private boolean constructor;
    private TypeModel returnType;
    private ParamModel[] params;
    private String[] modifiers;
    private String content;

    private AnnotationModel[] annotations;

    private String[] views;
    private Map<String, ViewContext> viewMappings;

    private ClassModel parent;

    public void printMethod() {
        if (skipInView()) {
            return;
        }

        printAnnotations();

        println(() -> {
            printModifiers();

            if (constructor) {
                String viewClassName = parent.getType().getViewName();
                print(" ", viewClassName, "(");
            } else {
                if (returnType != null) {
                    inView(getMappedView(), () -> {
                        print(returnType.getUsage());
                    });
                } else {
                    print("void");
                }

                print(" ", name, "(");
            }

            printParameters();

            print(")");

            if (!Set.of(modifiers).contains("abstract")) {
                print(" ");
                printContent();
            } else {
                print(";");
            }
        });

        println();

    }

    private void printAnnotations() {
        Arrays.
                stream(getAnnotations()).
                filter(Viewable::includeInView).
                map(AnnotationModel::toString).
                forEach(Printable::println);
    }

    private void printParameters() {
        String str = Arrays.
                stream(params).
                map(ParamModel::getDefinition).
                collect(Collectors.joining(", "));

        print(str);
    }

    private void printContent() {
        String content = addIndent(this.content);

        // if method contains local variable type inference (using 'var' for type)
        // then compiler would erase it and replace with a comment: /*missing*/,
        // however we need the keyword 'var' in our View class,
        // therefore a basic string replacement is a good enough workaround.
        if (content.contains("/*missing*/")) {
            content = content.replaceAll("/\\*missing\\*/", "var");
        }

        print(content);
    }
}
