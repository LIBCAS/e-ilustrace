package cz.inqool.entityviews.model;

import cz.inqool.entityviews.AbstractView;
import cz.inqool.entityviews.View;
import cz.inqool.entityviews.ViewContext;
import cz.inqool.entityviews.function.Printable;
import cz.inqool.entityviews.function.Viewable;
import cz.inqool.entityviews.model.type.RealTypeModel;
import cz.inqool.entityviews.model.type.TypeModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static cz.inqool.entityviews.context.ContextHolder.inView;
import static cz.inqool.entityviews.context.ContextHolder.indented;
import static cz.inqool.entityviews.function.Printable.print;
import static cz.inqool.entityviews.function.Printable.println;
import static java.util.Collections.emptyMap;

@AllArgsConstructor
@Getter
@Setter
public class ClassModel implements Viewable {

    private RealTypeModel type;
    private TypeModel superClass;
    private RealTypeModel viewLeaf;
    private RealTypeModel[] viewSubClasses;
    private ImplementModel[] implementModels;
    private boolean isAbstract;
    private String[] views;
    private boolean generateRef;
    private Map<String, ViewContext> viewMappings;

    private AnnotationModel[] annotations;
    private FieldModel[] fields;
    private MethodModel[] methods;

    private static final RealTypeModel embeddableType = new RealTypeModel(Embeddable.class.getPackageName(), Embeddable.class.getSimpleName(), new TypeModel[]{});
    private static final AnnotationModel embeddableAnnotation = new AnnotationModel(embeddableType, emptyMap(), null);

    public void printClass() {
        printAnnotations();

        println(() -> {
            print("public ");
            if (isAbstract) {
                print("abstract ");
            }

            print("class ", type.getDefinition());

            if (superClass != null) {
                printSuperClass();
            }

            printViewInterface();
            printInterfaces();


            print(" {");
        });

        indented(() -> {
            printFields();
            printMethods();

            if (isViewableLeafSuperclass()) {
                printToEntityForLeafSuperclassAbstractMethods();
                printToEntityForLeafSuperclassMethod();
                printToEntitiesForLeafSuperclassMethods();

                printToViewForSuperclassAbstractMethods();
                printToViewForLeafSuperclassMethod();
                printToViewsForLeafSuperclassMethods();
            } else if (isViewableLeafSubclass()) {
                printToEntityForLeafSubclassAbstractMethods();
                if (!isAbstract) {
                    printToEntityMethod();
                }
                printToEntityForLeafSubclassMethod();

                printToViewForLeafSubclassAbstractMethods();
                if (!isAbstract) {
                    printToViewMethod();
                }
                printToViewForLeafSubclassMethod();
            } else {
                printToEntityAbstractMethod();
                if (!isAbstract) {
                    printToEntityMethod();
                }
                printToEntitiesMethods();

                printToViewAbstractMethod();
                if (!isAbstract) {
                    printToViewMethod();
                }
                printToViewsMethods();
            }
        });

        println("}");
    }

    public void printRef() {
        String refClassName = type.getRefName();

        println(embeddableAnnotation.toString());
        println("public class ", refClassName, "{");

        indented(() -> {
            printRefIdField();
            printRefToEntityMethod();
            printRefToEntitiesMethod();
            printToRefMethod();
            printToRefsMethod();
        });

        println("}");
    }

    private void printAnnotations() {
        Arrays.
                stream(getAnnotations()).
                filter(Viewable::includeInView).
                map(AnnotationModel::toString).
                forEach(Printable::println);
    }

    private void printViewInterface() {
        print(" implements ");
        if (isAbstract) {
            print(AbstractView.class.getCanonicalName());
        } else {
            print(View.class.getCanonicalName());
        }
    }

    private void printSuperClass() {
        final String viewName = type.getDefinition();
        inView(getMappedView(), () -> {
            if (!superClass.isUsedView()) {
                System.err.println("View " + viewName + " extends a real entity and it should extend a view. You should use @ViewableMapping for all declared views when extending a superclass.");
            }

            print(" extends ", superClass.getUsage());
        });
    }

    private void printInterfaces() {
        if (implementModels.length > 0) {
            String str = Arrays.
                    stream(implementModels).
                    filter(Viewable::includeInView).
                    map(ImplementModel::getImplementedInterface).
                    map(TypeModel::getUsage).
                    collect(Collectors.joining(", "));

            if (str.length() > 0) {
                print(", ", str);
            }
        }

    }

    private void printFields() {
        for (FieldModel field : fields) {
            field.printDefinition();
        }

        if (fields.length > 0) {
            println();
        }
    }

    private void printMethods() {
        for (MethodModel method : methods) {
            method.printMethod();
        }
    }

    private void printToEntityAbstractMethod() {
        String parametrizedClassName = type.getUsageOriginal();
        String parametrizedViewClassName = type.getUsage();

        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print("void toEntity(", parametrizedClassName, " entity, ", parametrizedViewClassName, " view) {");
        });

        indented(() -> {
            println("if (view == null) {");
            indented(() -> {
                println("return;");
            });
            println("}");

            if (superClass != null) {
                inView(getMappedView(), () -> {
                    if (!superClass.isUsedView()) {
                        return;
                    }

                    println(superClass.getFullViewName(), ".toEntity(entity, view);");
                });
            }

            printToEntityFieldsAssignment();
        });

        println("}");
        println();
    }

    private void printToEntityForLeafSuperclassAbstractMethods() {
        String parametrizedClassName = type.getUsageOriginal();
        String parametrizedViewClassName = type.getUsage();

        // shorthand method
        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print("void toEntity(", parametrizedClassName, " entity, ", parametrizedViewClassName, " view) {");
        });

        indented(() -> {
            println("toEntity(entity, view, true);");
        });

        println("}");
        println();


        // full method
        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print("void toEntity(", parametrizedClassName, " entity, ", parametrizedViewClassName, " view, boolean callSub) {");
        });

        indented(() -> {
            println("if (view == null) {");
            indented(() -> {
                println("return;");
            });
            println("}");

            if (superClass != null) {
                inView(getMappedView(), () -> {
                    if (!superClass.isUsedView()) {
                        return;
                    }

                    println(superClass.getFullViewName(), ".toEntity(entity, view);");
                });
            }

            printToEntityFieldsAssignment();

            println("if (callSub) {");
            indented(() -> {
                for (RealTypeModel viewSubClass : viewSubClasses) {
                    println("if (view instanceof " + viewSubClass.getFullViewName() + ") {");
                    indented(() -> {
                        println(viewSubClass.getFullViewName() + ".toEntity((" + viewSubClass.getClassName() + ") entity, (" + viewSubClass.getFullViewName() + ") view, false);");
                        println("return;");
                    });
                    println("}");
                }
                if (isAbstract) {
                    println("throw new " + IllegalArgumentException.class.getSimpleName() + "(" +
                            "\"Type '\" + view.getClass() + \"' not recognized.\"" +
                            ");");
                }
            });
            println("}");
        });

        println("}");
        println();
    }

    private void printToEntityForLeafSubclassAbstractMethods() {
        String parametrizedClassName = type.getUsageOriginal();
        String parametrizedViewClassName = type.getUsage();

        String leafFullName = viewLeaf.getFullName();
        String leafViewFullName = viewLeaf.getFullViewName();

        // shorthand method
        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print("void toEntity(", parametrizedClassName, " entity, ", parametrizedViewClassName, " view) {");
        });

        indented(() -> {
            println("toEntity(entity, view, true);");
        });

        println("}");
        println();


        // full method
        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print("void toEntity(", parametrizedClassName, " entity, ", parametrizedViewClassName, " view, boolean callSuper) {");
        });

        indented(() -> {
            println("if (view == null) {");
            indented(() -> {
                println("return;");
            });
            println("}");

            if (superClass != null) {
                inView(getMappedView(), () -> {
                    if (!superClass.isUsedView()) {
                        return;
                    }

                    println("if (callSuper) {");
                    indented(() -> {
                        println(superClass.getFullViewName(), ".toEntity(entity, view, false);");
                    });
                    println("}");
                });
            }

            printToEntityFieldsAssignment();
        });

        println("}");
        println();


        // shorthand method for superclass context
        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print("void toEntity(", leafFullName, " entity, ", parametrizedViewClassName, " view) {");
        });

        indented(() -> {
            println("toEntity((" + parametrizedClassName + ") entity, view);");
        });

        println("}");
        println();
    }

    private void printToEntityMethod() {
        String parametrizedClassName = type.getUsageOriginal();
        String parametrizedViewClassName = type.getUsage();

        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print(parametrizedClassName, " toEntity(", parametrizedViewClassName, " view) {");
        });

        indented(() -> {
            println("if (view == null) {");
            indented(() -> {
                println("return null;");
            });
            println("}");

            println(parametrizedClassName, " entity = new ", parametrizedClassName, "();");
            println("toEntity(entity, view);");

            println("return entity;");
        });

        println("}");
        println();
    }

    private void printToEntityForLeafSuperclassMethod() {
        String parametrizedClassName = type.getUsageOriginal();
        String parametrizedViewClassName = type.getUsage();

        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print(parametrizedClassName, " toEntity(", parametrizedViewClassName, " view) {");
        });

        indented(() -> {
            println("if (view == null) {");
            indented(() -> {
                println("return null;");
            });
            println("}");
            println();

            for (RealTypeModel viewSubClass : viewSubClasses) {
                println("if (view instanceof " + viewSubClass.getFullViewName() + ") {");
                indented(() -> {
                    println("return " + viewSubClass.getFullViewName() + ".toEntity((" + viewSubClass.getFullViewName() + ") view);");
                });
                println("}");
            }

            if (isAbstract) {
                println("throw new " + IllegalArgumentException.class.getSimpleName() + "(" +
                        "\"Type '\" + view.getClass() + \"' not recognized.\"" +
                        ");");
            } else {
                println(parametrizedClassName, " entity = new ", parametrizedClassName, "();");
                println("toEntity(entity, view);");

                println("return entity;");
            }
        });

        println("}");
        println();
    }

    private void printToEntityForLeafSubclassMethod() {
        String parametrizedClassName = type.getUsageOriginal();
        String parametrizedViewClassName = type.getUsage();

        String leafFullName = viewLeaf.getFullName();
        String leafViewFullName = viewLeaf.getFullViewName();

        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print(leafFullName, " toEntity(", leafViewFullName, " view) {");
        });

        indented(() -> {
            println("return toEntity((" + parametrizedViewClassName + ") view);");
        });

        println("}");
        println();
    }

    private void printToEntitiesMethods() {
        if (isAbstract) {
            return;
        }

        printToEntitiesForLeafSuperclassMethods();
    }

    private void printToEntitiesForLeafSuperclassMethods() {
        String parametrizedClassName;
        String parametrizedViewClassName;
        if (viewLeaf != null && viewSubClasses == null) {
            parametrizedClassName = viewLeaf.getFullName();
            parametrizedViewClassName = viewLeaf.getFullViewName();
        } else {
            parametrizedClassName = type.getUsageOriginal();
            parametrizedViewClassName = type.getUsage();
        }

        // shorthand
        println(() -> {
            print("public static <");

            if (type.hasArguments()) {
                print(type.getArgumentsDefinition(), ", ");
            }

            print("EVCollection extends java.util.Collection<", parametrizedClassName, ">>");
            print("EVCollection toEntities(java.util.Collection<", parametrizedViewClassName, "> views, java.util.function.Supplier<EVCollection> supplier) {");
        });

        indented(() -> {
            println("return toEntities(views, supplier, ", parametrizedClassName, ".class);");
        });

        println("}");
        println();

        // full
        println(() -> {
            print("public static <");

            if (type.hasArguments()) {
                print(type.getArgumentsDefinition(), ", ");
            }
            //  print(type.getArguments()[0].getUsage(), ".toEntities(view.", name, ", ", implementation, "::new, ", type.getArguments()[0].getFullName(),".class);");
            print("EV extends ", parametrizedClassName, ", ");
            print("EVCollection extends java.util.Collection<EV>> ");
            print("EVCollection toEntities(java.util.Collection<? extends ", parametrizedViewClassName, "> views, java.util.function.Supplier<EVCollection> supplier, Class<EV> entityClass) {");
        });

        indented(() -> {
            println("if (views == null) {");
            indented(() -> {
                println("return null;");
            });

            println("}");
            println();

            println("return views.stream().map(view -> entityClass.cast(toEntity(view))).collect(java.util.stream.Collectors.toCollection(supplier));");
        });

        println("}");
        println();
    }

    private void printRefIdField() {
        println("public String id;");
        println();
    }

    private void printRefToEntityMethod() {
        String refClassName = type.getRefName();
        String parametrizedClassName = type.getUsageOriginal();

        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print(parametrizedClassName, " toEntity(", refClassName, " ref", ") {");
        });

        indented(() -> {
            println("if (ref == null) {");
            indented(() -> {
                println("return null;");
            });
            println("}");
            println();
            println(parametrizedClassName, " entity = new ", parametrizedClassName, "();");
            println("entity.id = ref.id;");
            println("return entity;");
        });

        println("}");
        println();
    }

    private void printRefToEntitiesMethod() {
        if (isAbstract) {
            return;
        }

        String parametrizedClassName = type.getUsageOriginal();
        String refClassName = type.getRefName();

        println(() -> {
            print("public static <");

            if (type.hasArguments()) {
                print(type.getArgumentsDefinition(), ", ");
            }

            print("EV extends ", parametrizedClassName, ", ");
            print("EVCollection extends java.util.Collection<", parametrizedClassName, ">>");
            print("EVCollection toEntities(java.util.Collection<", refClassName, "> refs, java.util.function.Supplier<EVCollection> supplier, Class<EV> entityClass) {");
        });

        indented(() -> {
            println("if (refs == null) {");
            indented(() -> {
                println("return null;");
            });

            println("}");
            println();

            println(() -> {
                print("return refs.stream().map(", refClassName, "::");

                if (type.hasArguments()) {
                    print("<", type.getArgumentsUsage(), ">");
                }

                print("toEntity).collect(java.util.stream.Collectors.toCollection(supplier));");
            });
        });

        println("}");
        println();
    }

    private void printToRefMethod() {
        String refClassName = type.getRefName();
        String parametrizedClassName = type.getUsageOriginal();

        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print(refClassName, " toRef(", parametrizedClassName, " entity) {");
        });

        indented(() -> {
            println("if (entity == null) {");
            indented(() -> {
                println("return null;");
            });
            println("}");
            println();

            println(refClassName, " ref = new ", refClassName, "();");
            println("ref.id = entity.id;");
            println("return ref;");
        });

        println("}");
        println();
    }

    private void printToRefsMethod() {
        if (isAbstract) {
            return;
        }

        String parametrizedClassName = type.getUsageOriginal();
        String refClassName = type.getRefName();

        // shorthand
        println(() -> {
            print("public static <");

            if (type.hasArguments()) {
                print(type.getArgumentsDefinition(), ", ");
            }

            print("EVCollection extends java.util.Collection<", refClassName, ">>");
            print("EVCollection toRefs(java.util.Collection<", parametrizedClassName, "> entities, java.util.function.Supplier<EVCollection> supplier) {");
        });

        indented(() -> {
            println("return toRefs(entities, supplier, ", parametrizedClassName, ".class);");
        });

        println("}");
        println();


        // full
        println(() -> {
            print("public static ");

            print("<");

            if (type.hasArguments()) {
                print(type.getArgumentsDefinition(), ", ");
            }

            print("EV extends ", parametrizedClassName, ", ");
            print("EVCollection extends java.util.Collection<", refClassName, ">>");
            print("EVCollection toRefs(java.util.Collection<", parametrizedClassName, "> entities, java.util.function.Supplier<EVCollection> supplier, Class<EV> entityClass) {");
        });

        indented(() -> {
            println("if (entities == null) {");
            indented(() -> {
                println("return null;");
            });

            println("}");
            println();

            println(() -> {
                print("return entities.stream().map(", refClassName, "::");

                if (type.hasArguments()) {
                    print("<", type.getArgumentsUsage(), ">");
                }

                print("toRef).collect(java.util.stream.Collectors.toCollection(supplier));");
            });

        });

        println("}");
        println();
    }

    private void printToViewAbstractMethod() {
        String parametrizedClassName = type.getUsageOriginal();
        String parametrizedViewClassName = type.getUsage();

        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print("void toView(", parametrizedViewClassName, " view, ", parametrizedClassName, " entity) {");
        });

        indented(() -> {
            println("if (entity == null) {");
            indented(() -> {
                println("return;");
            });

            println("}");

            if (superClass != null) {
                inView(getMappedView(), () -> {
                    if (!superClass.isUsedView()) {
                        return;
                    }

                    println(superClass.getFullViewName(), ".toView(view, entity);");
                });
            }

            printToViewFieldsAssignment();
        });

        println("}");
        println();
    }

    private void printToViewForSuperclassAbstractMethods() {
        String parametrizedClassName = type.getUsageOriginal();
        String parametrizedViewClassName = type.getUsage();

        // shorthand method
        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print("void toView(", parametrizedViewClassName, " view, ", parametrizedClassName, " entity) {");
        });

        indented(() -> {
            println("toView(view, entity, true);");
        });

        println("}");
        println();


        // full method
        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print("void toView(", parametrizedViewClassName, " view, ", parametrizedClassName, " entity, boolean callSub) {");
        });

        indented(() -> {
            println("if (entity == null) {");
            indented(() -> {
                println("return;");
            });

            println("}");

            if (superClass != null) {
                inView(getMappedView(), () -> {
                    if (!superClass.isUsedView()) {
                        return;
                    }

                    println(superClass.getFullViewName(), ".toView(view, entity);");
                });
            }

            printToViewFieldsAssignment();

            println("if (callSub) {");
            indented(() -> {
                for (RealTypeModel viewSubClass : viewSubClasses) {
                    println("if (entity instanceof " + viewSubClass.getClassName() + ") {");
                    indented(() -> {
                        println(viewSubClass.getFullViewName() + ".toView((" + viewSubClass.getFullViewName() + ") view, (" + viewSubClass.getClassName() + ") entity, false);");
                        println("return;");
                    });
                    println("}");
                }
                if (isAbstract) {
                    println("throw new " + IllegalArgumentException.class.getSimpleName() + "(" +
                            "\"Type '\" + entity.getClass() + \"' not recognized.\"" +
                            ");");
                }
            });

            println("}");
        });

        println("}");
        println();
    }

    private void printToViewForLeafSubclassAbstractMethods() {
        String parametrizedClassName = type.getUsageOriginal();
        String parametrizedViewClassName = type.getUsage();

        String leafFullName = viewLeaf.getFullName();
        String leafViewFullName = viewLeaf.getFullViewName();

        // shorthand method
        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print("void toView(", parametrizedViewClassName, " view, ", parametrizedClassName, " entity) {");
        });

        indented(() -> {
            println("toView(view, entity, true);");
        });

        println("}");
        println();


        // full method
        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print("void toView(", parametrizedViewClassName, " view, ", parametrizedClassName, " entity, boolean callSuper) {");
        });

        indented(() -> {
            println("if (entity == null) {");
            indented(() -> {
                println("return;");
            });
            println("}");

            if (superClass != null) {
                inView(getMappedView(), () -> {
                    if (!superClass.isUsedView()) {
                        return;
                    }

                    println("if (callSuper) {");
                    indented(() -> {
                        println(superClass.getFullViewName(), ".toView(view, entity, false);");
                    });
                    println("}");
                });
            }

            printToViewFieldsAssignment();
        });

        println("}");
        println();


        // shorthand method for superclass context
        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print("void toView(", leafViewFullName, " view, ", leafFullName, " entity) {");
        });

        indented(() -> {
            println("toView((" + parametrizedViewClassName + ") view, (" + parametrizedClassName + ") entity);");
        });

        println("}");
        println();
    }

    private void printToViewMethod() {
        String parametrizedClassName = type.getUsageOriginal();
        String parametrizedViewClassName = type.getUsage();

        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print(parametrizedViewClassName, " toView(", parametrizedClassName, " entity) {");
        });

        indented(() -> {
            println("if (entity == null) {");
            indented(() -> {
                println("return null;");
            });

            println("}");

            println(parametrizedViewClassName, " view = new ", parametrizedViewClassName, "();");
            println("toView(view, entity);");

            println("return view;");
        });

        println("}");
        println();
    }

    private void printToViewForLeafSuperclassMethod() {
        String parametrizedClassName = type.getUsageOriginal();
        String parametrizedViewClassName = type.getUsage();

        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print(parametrizedViewClassName, " toView(", parametrizedClassName, " entity) {");
        });

        indented(() -> {
            println("if (entity == null) {");
            indented(() -> {
                println("return null;");
            });
            println("}");
            println();

            for (RealTypeModel viewSubClass : viewSubClasses) {
                println("if (entity instanceof " + viewSubClass.getClassName() + ") {");
                indented(() -> {
                    println("return " + viewSubClass.getFullViewName() + ".toView((" + viewSubClass.getClassName() + ") entity);");
                });
                println("}");
            }
            if (isAbstract) {
                println("throw new " + IllegalArgumentException.class.getSimpleName() + "(" +
                        "\"Type '\" + entity.getClass() + \"' not recognized.\"" +
                        ");");
            } else {
                println(parametrizedViewClassName, " view = new ", parametrizedViewClassName, "();");
                println("toView(view, entity);");

                println("return view;");
            }
        });

        println("}");
        println();
    }

    private void printToViewForLeafSubclassMethod() {
        String parametrizedClassName = type.getUsageOriginal();
        String parametrizedViewClassName = type.getUsage();

        String leafFullName = viewLeaf.getFullName();
        String leafViewFullName = viewLeaf.getFullViewName();

        println(() -> {
            print("public static ");

            if (type.hasArguments()) {
                print("<", type.getArgumentsDefinition(), "> ");
            }

            print(leafViewFullName, " toView(", leafFullName, " entity) {");
        });

        indented(() -> {
            println("return toView((" + parametrizedClassName + ") entity);");
        });

        println("}");
        println();
    }

    private void printToViewsMethods() {
        if (isAbstract) {
            return;
        }

        printToViewsForLeafSuperclassMethods();
    }

    private void printToViewsForLeafSuperclassMethods() {
        String parametrizedClassName;
        String parametrizedViewClassName;
        if (viewLeaf != null && viewSubClasses == null) {
            parametrizedClassName = viewLeaf.getFullName();
            parametrizedViewClassName = viewLeaf.getFullViewName();
        } else {
            parametrizedClassName = type.getUsageOriginal();
            parametrizedViewClassName = type.getUsage();
        }

        // shorthand
        println(() -> {
            print("public static ");

            print("<");

            if (type.hasArguments()) {
                print(type.getArgumentsDefinition(), ", ");
            }

            print("EVCollection extends java.util.Collection<", parametrizedViewClassName, ">>");
            print("EVCollection toViews(java.util.Collection<", parametrizedClassName, "> entities, java.util.function.Supplier<EVCollection> supplier) {");
        });

        indented(() -> {
            println("return toViews(entities, supplier, ", parametrizedViewClassName, ".class);");
        });

        println("}");
        println();

        // full
        println(() -> {
            print("public static <");

            if (type.hasArguments()) {
                print(type.getArgumentsDefinition(), ", ");
            }

            print("EV extends ", parametrizedViewClassName, ", ");
            print("EVCollection extends java.util.Collection<EV>> ");
            print("EVCollection toViews(java.util.Collection<? extends ", parametrizedClassName, "> entities, java.util.function.Supplier<EVCollection> supplier, Class<EV> viewClass) {");
        });

        indented(() -> {
            println("if (entities == null) {");
            indented(() -> {
                println("return null;");
            });

            println("}");
            println();

            println("return entities.stream().map(entity -> viewClass.cast(toView(entity))).collect(java.util.stream.Collectors.toCollection(supplier));");
        });

        println("}");
        println();
    }

    private void printToEntityFieldsAssignment() {
        for (FieldModel field : fields) {
            field.printToEntityAssignment();
        }
    }

    private void printToViewFieldsAssignment() {
        for (FieldModel field : fields) {
            field.printToViewAssignment();
        }
    }

    private boolean isViewableLeafSubclass() {
        return viewLeaf != null && viewSubClasses == null;
    }

    private boolean isViewableLeafSuperclass() {
        return viewLeaf != null && viewSubClasses != null;
    }
}
