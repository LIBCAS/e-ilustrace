package cz.inqool.entityviews.model;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import cz.inqool.entityviews.ViewContext;
import cz.inqool.entityviews.function.Accessible;
import cz.inqool.entityviews.function.Viewable;
import cz.inqool.entityviews.model.annotation.HibernateManyToManyAnnotationModel;
import cz.inqool.entityviews.model.annotation.HibernateOneToManyAnnotationModel;
import cz.inqool.entityviews.model.type.RealTypeModel;
import cz.inqool.entityviews.model.type.TypeModel;
import cz.inqool.entityviews.model.type.WildcardTypeModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static cz.inqool.entityviews.context.ContextHolder.getContext;
import static cz.inqool.entityviews.context.ContextHolder.inView;
import static cz.inqool.entityviews.function.Printable.print;
import static cz.inqool.entityviews.function.Printable.println;
import static cz.inqool.entityviews.model.AnnotationModel.escapeString;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

@AllArgsConstructor
@Getter
@Setter
public class FieldModel implements Accessible, Viewable {
    private String name;
    private TypeModel type;
    private String value;
    private String[] modifiers;

    private AnnotationModel[] annotations;

    private String[] views;
    private Map<String, ViewContext> viewMappings;

    private static final Converter<String, String> setterNameConverter = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL);

    private static final RealTypeModel columnType = new RealTypeModel(Column.class.getPackageName(), Column.class.getSimpleName(), new TypeModel[]{});
    private static final RealTypeModel oneToManyType = new RealTypeModel(OneToMany.class.getPackageName(), OneToMany.class.getSimpleName(), new TypeModel[]{});
    private static final RealTypeModel manyToManyType = new RealTypeModel(ManyToMany.class.getPackageName(), ManyToMany.class.getSimpleName(), new TypeModel[]{});
    private static final RealTypeModel joinColumnType = new RealTypeModel(JoinColumn.class.getPackageName(), JoinColumn.class.getSimpleName(), new TypeModel[]{});
    private static final RealTypeModel joinTableType = new RealTypeModel(JoinTable.class.getPackageName(), JoinTable.class.getSimpleName(), new TypeModel[]{});
    private static final RealTypeModel elementCollectionType = new RealTypeModel(ElementCollection.class.getPackageName(), ElementCollection.class.getSimpleName(), new TypeModel[]{});
    private static final RealTypeModel collectionTableType = new RealTypeModel(CollectionTable.class.getPackageName(), CollectionTable.class.getSimpleName(), new TypeModel[]{});

    private static final RealTypeModel embeddedType = new RealTypeModel(Embedded.class.getPackageName(), Embedded.class.getSimpleName(), new TypeModel[]{});
    private static final RealTypeModel attributeOverrideType = new RealTypeModel(AttributeOverride.class.getPackageName(), AttributeOverride.class.getSimpleName(), new TypeModel[]{});

    private static final AnnotationModel embeddedAnnotation = new AnnotationModel(embeddedType, emptyMap(), null);

    public void printDefinition() {
        if (skipInView()) {
            return;
        }

        ViewContext classView = getContext().getView();

        inView(getMappedView(), () -> {
            printAnnotations(classView);

            println(() -> {
                printModifiers();

                print(type.getUsage(), " ", name);

                if (value != null) {
                    print(" = ", value);
                }

                print(";");
            });
            println();
        });
    }

    public void printToEntityAssignment() {
        if (skipInView()) {
            return;
        }

        inView(getMappedView(), () -> {
            boolean isCollection = type.isCollection();
            boolean isUsedView = isCollection ? type.getArguments()[0].isUsedView() : type.isUsedView();


            if (isUsedView) {
                println(() -> {
                    print("entity.", name, " = ");

                    if (isCollection) {
                        String implementation = type.getCollectionImplementation();
                        TypeModel argumentType = type.getArguments()[0];
                        String argumentTypeUsage = (argumentType instanceof WildcardTypeModel)
                                ? ((WildcardTypeModel) argumentType).getPlainUsage()
                                : argumentType.getUsage();
                        print(argumentTypeUsage, ".toEntities(view.", name, ", ", implementation, "::new, ", argumentType.getFullName(), ".class);");
                    } else {
                        print(type.getUsage(), ".toEntity(view.", name, ");");
                    }
                });

                if (isOneWay()) {
                    String oneWayTarget = getOneWayTarget();
                    String fieldSetter = "set" + setterNameConverter.convert(oneWayTarget);

                    println(() -> {
                        if (isCollection) {
                            print("if (entity.", name, " != null) entity.", name, ".stream().filter(o -> o != null).forEach(o -> o.", fieldSetter, "(entity));");
                        } else {
                            print("if (entity.", name, " != null) entity.", name, ".", fieldSetter, "(entity);");
                        }
                    });
                }
            } else {
                println("entity.", name, " = view.", name, ";");
            }
        });
    }

    public void printToViewAssignment() {
        if (skipInView()) {
            return;
        }

        inView(getMappedView(), () -> {
            boolean isCollection = type.isCollection();
            boolean isUsedView = isCollection ? type.getArguments()[0].isUsedView() : type.isUsedView();

            if (isUsedView) {
                println(() -> {
                    print("view.", name, " = ");

                    String method = isRef() ? "toRef" : "toView";

                    if (isCollection) {
                        String implementation = type.getCollectionImplementation();
                        TypeModel argumentType = type.getArguments()[0];
                        String argumentTypeUsage = (argumentType instanceof WildcardTypeModel)
                                ? ((WildcardTypeModel) argumentType).getPlainUsage()
                                : argumentType.getUsage();
                        print(argumentTypeUsage, ".", method, "s(entity.", name, ", ", implementation, "::new, ", argumentType.getFullViewName(), ".class);");
                    } else {
                        print(type.getUsage(), ".", method, "(entity.", name, ");");
                    }

                });
            } else {
                println("view.", name, " = entity.", name, ";");
            }
        });
    }

    private void printAnnotations(ViewContext view) {
        boolean foundJoinColumn = false;
        boolean foundJoinTable = false;

        for (AnnotationModel annotation : getAnnotations()) {
            if (annotation.includeInView(view)) {
                String name = annotation.getType().getFullName();

                if (annotation.getType().equals(joinColumnType)) {
                    foundJoinColumn = true;
                }

                if (annotation.getType().equals(joinTableType)) {
                    foundJoinTable = true;
                }

                if (isRef() && !this.type.isCollection()) {
                    // skip
                    if (name.equals(ManyToOne.class.getCanonicalName())) {
                        continue;
                    }
                    if (name.equals(OneToOne.class.getCanonicalName())) {
                        continue;
                    }

                    // replace
                    if (name.equals(JoinColumn.class.getCanonicalName())) {
                        Object columnName = annotation.getAttributes().get("name");
                        printRefAnnotation(columnName);

                        continue;
                    }
                } else if (isRef() && this.type.isCollection()) {
                    // replace
                    if (name.equals(ManyToMany.class.getCanonicalName())) {
                        Map<String, Object> attributes = new HashMap<>();
                        Object fetchType = annotation.getAttributes().get("fetch");
                        if (fetchType != null) {
                            attributes.put("fetch", fetchType);
                        }
                        AnnotationModel elementCollectionAnnotation = new AnnotationModel(elementCollectionType, attributes, null);
                        println(elementCollectionAnnotation.toString());
                        continue;
                    }

                    if (name.equals(JoinTable.class.getCanonicalName())) {
                        Map<String, Object> attributes = new HashMap<>(annotation.getAttributes());
                        AnnotationArrayModel inverseJoinColumns = (AnnotationArrayModel) attributes.remove("inverseJoinColumns");
                        AnnotationModel joinColumn = (AnnotationModel) inverseJoinColumns.getObjects()[0];
                        String columnName = (String) joinColumn.getAttributes().get("name");

                        printRefAnnotation(columnName);

                        AnnotationModel collectionTableAnnotation = new AnnotationModel(collectionTableType, attributes, null);
                        println(collectionTableAnnotation.toString());
                        continue;
                    }
                } else if (this.type.isCollection() && isOneWay()) {
                    // replace
                    if (name.equals(OneToMany.class.getCanonicalName()) || name.equals(ManyToMany.class.getCanonicalName())) {
                        Map<String, Object> attributes = new HashMap<>(annotation.getAttributes());
                        Object columnName = attributes.remove("mappedBy");

                        if (columnName instanceof String) {
                            String str = (String) columnName;
                            columnName = escapeString(RealTypeModel.toSnakeCase(str.substring(1, str.length() - 1)) + "_id");
                        }

                        AnnotationModel xToManyAnnotation = name.equals(OneToMany.class.getCanonicalName())
                                ? HibernateOneToManyAnnotationModel.of(oneToManyType, attributes, null)
                                : HibernateManyToManyAnnotationModel.of(manyToManyType, attributes, null);
                        println(xToManyAnnotation.toString());

                        AnnotationModel joinColumnAnnotation = new AnnotationModel(joinColumnType, singletonMap("name", columnName), null);
                        println(joinColumnAnnotation.toString());

                        continue;
                    }
                }

                println(annotation.toString());
            }
        }

        if (isRef()) {
            // if no @JoinColumn annotation was found and we are printing a ref,
            // we need to derive the column name from field name
            if (!this.type.isCollection() && !foundJoinColumn) {
                printRefAnnotation(escapeString(RealTypeModel.toSnakeCase(name) + "_id"));
            }

            // if no @JoinTable annotation was found and we are printing a ref,
            // we need to derive the column name from field name
            if (this.type.isCollection() && !foundJoinTable) {
                printRefAnnotation(escapeString(RealTypeModel.toSnakeCase(name) + "_id"));

                throw new RuntimeException("Missing @JoinTable annotation. Deriving relationship table name from entity name is not supported.");
            }
        }
    }

    private void printRefAnnotation(Object columnName) {
        AnnotationModel columnAnnotation = new AnnotationModel(columnType, singletonMap("name", columnName), null);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", escapeString("id"));
        params.put("column", columnAnnotation);
        AnnotationModel attributeOverrideAnnotation = new AnnotationModel(attributeOverrideType, params, null);
        println(attributeOverrideAnnotation.toString());

        if (!this.type.isCollection()) {
            println(embeddedAnnotation.toString());
        }
    }

    private boolean isRef() {
        ViewContext view = getContext().getView();
        return view.isUseRef();
    }

    private boolean isOneWay() {
        ViewContext view = getContext().getView();
        return view.isUseOneWay();
    }

    private String getOneWayTarget() {
        ViewContext view = getContext().getView();
        return view.getOneWayTarget();
    }
}
