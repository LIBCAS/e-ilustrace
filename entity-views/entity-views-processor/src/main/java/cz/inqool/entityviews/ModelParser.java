package cz.inqool.entityviews;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.util.Pair;
import cz.inqool.entityviews.model.*;
import cz.inqool.entityviews.model.annotation.*;
import cz.inqool.entityviews.model.type.RealTypeModel;
import cz.inqool.entityviews.model.type.TemplateModel;
import cz.inqool.entityviews.model.type.TypeModel;
import cz.inqool.entityviews.model.type.WildcardTypeModel;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.persistence.OneToMany;
import java.util.*;

public class ModelParser {
    private JavacElements elementUtils;
    private Trees trees;
    private Set<String> markingAnnotations;

    public void init(JavacElements elementUtils, Trees trees, RoundEnvironment roundEnv) {
        this.elementUtils = elementUtils;
        this.trees = trees;

        markingAnnotations = AnnotationUtils.getMarkingAnnotations(roundEnv);
    }

    public UnitModel parseClass(ClassSymbol clazz) {
        String[] views = extractClassViews(clazz);
        boolean generateRef = extractGenerateRef(clazz);
        AnnotationModel[] annotations = extractAnnotations(clazz);
        FieldModel[] fields = extractFields(clazz);
        MethodModel[] methods = extractMethods(clazz);
        ImportModel[] imports = extractImports(clazz);
        TypeModel superClass = extractSuperClass(clazz);
        ImplementModel[] implementModels = extractInterfaces(clazz);
        RealTypeModel type = parseRealType(clazz.asType());
        Map<String, ViewContext> viewsMappings = parseViewsMapping(clazz, superClass);

        boolean isAbstract = clazz.getModifiers().contains(Modifier.ABSTRACT);
        RealTypeModel leafClass = findLeafClass(clazz);
        RealTypeModel[] leafSubclasses = findLeafSubclasses(clazz);

        ClassModel classModel = new ClassModel(type, superClass, leafClass, leafSubclasses, implementModels, isAbstract, views,
                generateRef, viewsMappings, annotations, fields, methods);

        Arrays.stream(methods).forEach(method -> method.setParent(classModel));

        return new UnitModel(imports, classModel);
    }

    private TypeModel extractSuperClass(ClassSymbol clazz) {
        JCTree.JCClassDecl classDecl = getClassDecl(clazz);

        JCTree.JCExpression extending = classDecl.extending;
        if (extending != null) {
            return parseType(clazz.getSuperclass());
        } else {
            return null;
        }
    }

    private RealTypeModel findLeafClass(ClassSymbol clazz) {
        Type.ClassType type = (Type.ClassType) clazz.type;
        while (true) {
            ViewableLeaf viewableLeaf = AnnotationUtils.getAnnotation(type.tsym, ViewableLeaf.class);
            if (viewableLeaf != null) {
                return parseRealType(type);
            }

            if (type.supertype_field instanceof Type.ClassType) {
                type = (Type.ClassType) type.supertype_field;
            } else {
                return null;
            }
        }
    }

    private RealTypeModel[] findLeafSubclasses(ClassSymbol clazz) {
        Type.ClassType type = (Type.ClassType) clazz.type;
        ViewableLeaf viewableLeaf = AnnotationUtils.getAnnotation(type.tsym, ViewableLeaf.class);
        if (viewableLeaf != null) {
            List<RealTypeModel> subClassTypeModels = new ArrayList<>();

            try {
                viewableLeaf.subClasses();
            } catch (MirroredTypesException ex) {
                for (TypeMirror mirror : ex.getTypeMirrors()) {
                    try {
                        RealTypeModel typeModel = parseRealType(((Type) mirror).tsym.type);
                        subClassTypeModels.add(typeModel);
                    } catch (MirroredTypesException ignored) {
                    }
                }
            }

            return subClassTypeModels.toArray(RealTypeModel[]::new);
        }

        return null;
    }

    private ImplementModel[] extractInterfaces(ClassSymbol clazz) {
        JCTree.JCClassDecl classDecl = getClassDecl(clazz);

        return classDecl.implementing.
                stream().
                map(inter -> {
                    TypeModel type = parseType(inter.type);
                    String[] views = extractImplementsViews(clazz, inter.type);
                    return new ImplementModel(type, views);
                }).
                toArray(ImplementModel[]::new);
    }

    private String[] extractImplementsViews(Element element, Type type) {
        ViewableImplement[] viewableImplements = AnnotationUtils.getAnnotations(element, ViewableImplement.class);

        List<String> views = new ArrayList<>();
        boolean hit = false;

        for (ViewableImplement viewableImplement : viewableImplements) {
            try {
                viewableImplement.value();
            } catch (MirroredTypesException ex) {
                for (TypeMirror mirror : ex.getTypeMirrors()) {
                    if (Objects.equals(((Type) mirror).tsym.erasure_field, type.tsym.erasure_field)) {
                        hit = true;
                        views.addAll(List.of(viewableImplement.views()));
                    }
                }
            }
        }

        if (hit) {
            return views.toArray(String[]::new);
        }

        return null;
    }

    private String[] extractAnnotationViews(Element element, Type type) {

        ViewableAnnotation[] viewableAnnotations = AnnotationUtils.getAnnotations(element, ViewableAnnotation.class);

        List<String> views = new ArrayList<>();

        for (ViewableAnnotation viewableAnnotation : viewableAnnotations) {
            try {
                viewableAnnotation.value();
            } catch (MirroredTypesException ex) {
                for (TypeMirror mirror : ex.getTypeMirrors()) {
                    if (Objects.equals(((Type) mirror).tsym.erasure_field, type.tsym.erasure_field)) {
                        views.addAll(List.of(viewableAnnotation.views()));
                    }
                }
            }
        }

        if (views.size() > 0) {
            return views.toArray(String[]::new);
        }

        return null;
    }

    private String[] extractClassViews(Element element) {
        ViewableClass[] viewableClass = AnnotationUtils.getAnnotations(element, ViewableClass.class);

        if (viewableClass.length == 0) {
            throw new RuntimeException("At least one @ViewableClass annotation needs to be found.");
        }

        return Arrays.stream(viewableClass)
                .flatMap(c -> Arrays.stream(c.views()))
                .toArray(String[]::new);
    }

    private boolean extractGenerateRef(Element element) {
        ViewableClass viewableClass = AnnotationUtils.getAnnotation(element, ViewableClass.class);
        return viewableClass.generateRef();
    }

    private AnnotationModel[] extractAnnotations(Element element) {

        JCTree.JCModifiers modifiers = extractModifiers(element);

        if (modifiers != null) {
            List<JCTree.JCAnnotation> jcAnnotations = modifiers.getAnnotations();

            return jcAnnotations.
                    stream().
                    filter(this::filterOutViewAnnotations).
                    map(annotation -> parseAnnotationMirror(element, annotation)).
                    toArray(AnnotationModel[]::new);
        } else {
            return new AnnotationModel[]{};
        }
    }

    private JCTree.JCModifiers extractModifiers(Element element) {
        JCTree tree = elementUtils.getTree(element);

        if (tree instanceof JCTree.JCClassDecl) {
            return ((JCTree.JCClassDecl) tree).getModifiers();
        } else if (tree instanceof JCTree.JCMethodDecl) {
            return ((JCTree.JCMethodDecl) tree).getModifiers();
        } else if (tree instanceof JCTree.JCVariableDecl) {
            return ((JCTree.JCVariableDecl) tree).getModifiers();
        } else {
            return null;
        }
    }

    private boolean filterOutViewAnnotations(JCTree.JCAnnotation annotation) {
        String className = annotation.getAnnotationType().type.toString();

        return !markingAnnotations.contains(className);
    }

    private AnnotationModel parseAnnotationMirror(Element element, JCTree.JCAnnotation annotation) {
        TypeModel type = parseType(annotation.type);
        String[] views = extractAnnotationViews(element, annotation.type);

        return parseAnnotation(annotation.attribute, views);
    }

    private AnnotationModel parseAnnotation(Attribute.Compound annotation, String[] views) {
        TypeModel type = parseType(annotation.type);

        HashMap<String, Object> attributes = new LinkedHashMap<>();
        for (Pair<Symbol.MethodSymbol, Attribute> pair : annotation.values) {
            String attrName = pair.fst.name.toString();
            Object attrValue;

            if (pair.snd instanceof Attribute.Compound) {
                attrValue = parseAnnotation((Attribute.Compound) pair.snd, null);
            } else if (pair.snd instanceof Attribute.Array) {
                attrValue = parseAnnotationArray((Attribute.Array) pair.snd);
            } else {
                attrValue = pair.snd.toString();
            }

            attributes.put(attrName, attrValue);
        }

        return createAnnotationModel(type, annotation, attributes, views);
    }

    private AnnotationModel createAnnotationModel(TypeModel typeModel, Attribute.Compound annotation, Map<String, Object> attributes, String[] views) {
        if (typeModel instanceof RealTypeModel) {
            RealTypeModel realTypeModel = (RealTypeModel) typeModel;

            Map<Symbol.MethodSymbol, Attribute> elementValues = elementUtils.getElementValuesWithDefaults(annotation);

            if (realTypeModel.getPackageName().equals("com.fasterxml.jackson.annotation") && realTypeModel.getClassName().equals("JsonSubTypes.Type")) {
                return JsonSubTypesTypeAnnotationModel.of(typeModel, attributes, elementValues, views);
            }
            if (realTypeModel.getPackageName().equals("io.swagger.v3.oas.annotations.media") && realTypeModel.getClassName().equals("DiscriminatorMapping")) {
                return SwaggerDiscriminatorMappingAnnotationModel.of(typeModel, attributes, elementValues, views);
            }
            if (realTypeModel.getPackageName().equals("io.swagger.v3.oas.annotations.media") && realTypeModel.getClassName().equals("Schema")) {
                return SwaggerSchemaAnnotationModel.of(typeModel, attributes, elementValues, views);
            }
            if (realTypeModel.getPackageName().equals("javax.persistence") && realTypeModel.getClassName().equals("ManyToOne")) {
                return HibernateManyToOneAnnotationModel.of(typeModel, attributes, elementValues, views);
            }
            if (realTypeModel.getPackageName().equals("javax.persistence") && realTypeModel.getClassName().equals("OneToOne")) {
                return HibernateOneToOneAnnotationModel.of(typeModel, attributes, elementValues, views);
            }
            if (realTypeModel.getPackageName().equals("javax.persistence") && realTypeModel.getClassName().equals("OneToMany")) {
                return HibernateOneToManyAnnotationModel.of(typeModel, attributes, elementValues, views);
            }
            if (realTypeModel.getPackageName().equals("javax.persistence") && realTypeModel.getClassName().equals("ManyToMany")) {
                return HibernateManyToManyAnnotationModel.of(typeModel, attributes, elementValues, views);
            }
        }

        return new AnnotationModel(typeModel, attributes, views);
    }

    private AnnotationArrayModel parseAnnotationArray(Attribute.Array array) {
        Object[] objects = Arrays.
                stream(array.values).
                map(value -> {
                    if (value instanceof Attribute.Array) {
                        return parseAnnotationArray((Attribute.Array) value);
                    } else if (value instanceof Attribute.Compound) {
                        return parseAnnotation((Attribute.Compound) value, null);
                    } else {
                        return value.toString();
                    }
                }).
                toArray(Object[]::new);

        return new AnnotationArrayModel(objects);
    }

    private FieldModel[] extractFields(ClassSymbol clazz) {
        List<Symbol> members = clazz.getEnclosedElements();

        return members.
                stream().
                filter(member -> member.getKind() == ElementKind.FIELD).
                map(member -> (Symbol.VarSymbol) member).
                filter(member -> !member.isStatic()).
                map(this::parseField).
                toArray(FieldModel[]::new);
    }

    private FieldModel parseField(Symbol.VarSymbol field) {
        String fieldName = field.getSimpleName().toString();
        JCTree.JCVariableDecl variableDecl = getVariableDecl(field);

        TypeModel type = parseType(field.type);
        String value = variableDecl.init != null ? variableDecl.init.toString() : null;
        String[] modifiers = parseModifiers(field.getModifiers());

        AnnotationModel[] annotations = extractAnnotations(field);
        String[] views = parseViews(field);
        Map<String, ViewContext> viewsMappings = parseViewsMapping(field, type);

        return new FieldModel(fieldName, type, value, modifiers, annotations, views, viewsMappings);
    }

    private TypeModel parseType(Type argument) {
        if (argument instanceof Type.ClassType || argument instanceof Type.JCPrimitiveType) {
            return parseRealType(argument);
        } else if (argument instanceof Type.WildcardType) {
            return parseWildcardType((Type.WildcardType) argument);
        } else if (argument instanceof Type.TypeVar) {
            return parseTemplate((Type.TypeVar) argument);
        } else {
            return null;
        }
    }

    private TemplateModel parseTemplate(Type.TypeVar type) {
        Symbol.TypeSymbol symbol = type.tsym;
        String name = symbol.name.toString();

        TypeModel baseType = parseType(symbol.type.getUpperBound());

        return new TemplateModel(name, baseType);
    }

    private RealTypeModel parseRealType(Type type) {
        Symbol.TypeSymbol symbol = type.tsym;

        String packageName = symbol.packge().fullname.toString();
        String className = getEnclosingClassName(symbol);

        if (Objects.equals(packageName, "")) {
            packageName = null;
        }

        TypeModel[] arguments = type.
                allparams().
                map(this::parseType).
                toArray(TypeModel[]::new);


        return new RealTypeModel(packageName, className, arguments);
    }

    private WildcardTypeModel parseWildcardType(Type.WildcardType type) {
        RealTypeModel realTypeModel = parseRealType(type.type);
        BoundKind kind = type.kind;

        return new WildcardTypeModel(realTypeModel, kind.toString());
    }

    private String getEnclosingClassName(Symbol.TypeSymbol symbol) {
        String enclosingPath = "";
        if (symbol.getEnclosingElement() instanceof ClassSymbol) {
            enclosingPath = getEnclosingClassName((Symbol.TypeSymbol) symbol.getEnclosingElement()) + ".";
        }

        return enclosingPath + symbol.name.toString();
    }

    private MethodModel[] extractMethods(ClassSymbol clazz) {
        List<? extends Element> members = clazz.getEnclosedElements();

        return members.
                stream().
                filter(member -> member.getKind() == ElementKind.METHOD || member.getKind() == ElementKind.CONSTRUCTOR).
                map(member -> (Symbol.MethodSymbol) member).
                filter(member -> (member.flags() & 0x1000000000L) == 0).    // filter out synthetic constructors
                        map(this::parseMethod).
                toArray(MethodModel[]::new);
    }

    private MethodModel parseMethod(Symbol.MethodSymbol method) {
        String methodName = method.getSimpleName().toString();
        String[] modifiers = parseModifiers(method.getModifiers());

        JCTree.JCMethodDecl tree = getMethodDecl(method);
        String content = null;
        if (tree.getBody() != null) { // omit when abstract method
            content = tree.getBody().toString();
        }
        TypeModel type = parseType(method.getReturnType());
        ParamModel[] params = parseMethodParameters(method);

        AnnotationModel[] annotations = extractAnnotations(method);
        String[] views = parseViews(method);
        Map<String, ViewContext> viewsMappings = parseViewsMapping(method, type);

        boolean constructor = method.getKind() == ElementKind.CONSTRUCTOR;

        return new MethodModel(methodName, constructor, type, params, modifiers,
                content, annotations, views, viewsMappings, null);
    }

    private ParamModel[] parseMethodParameters(Symbol.MethodSymbol method) {
        return method.
                getParameters().
                stream().
                map(this::parseMethodParameter).
                toArray(ParamModel[]::new);
    }

    private ParamModel parseMethodParameter(Symbol.VarSymbol param) {
        String name = param.name.toString();
        TypeModel type = parseType(param.type);

        return new ParamModel(name, type);
    }

    private String[] parseViews(Element element) {
        ViewableProperty viewableProperty = element.getAnnotation(ViewableProperty.class);

        if (viewableProperty == null) {
            return null;
        }

        return viewableProperty.views();
    }

    private Map<String, ViewContext> parseViewsMapping(Element element, TypeModel type) {
        ViewableMapping[] mappings = AnnotationUtils.getAnnotations(element, ViewableMapping.class);

        Map<String, ViewContext> viewMappings = new HashMap<>();

        for (ViewableMapping mapping : mappings) {
            if (type == null) {
                String annotationName = mapping.annotationType().getSimpleName();
                throw new RuntimeException(String.format("Java type for a mapping in '@%s' located at element '%s' is not present.%sIf using '@%s' for class then this class MUST extend some superclass (such superclass is then the type).",
                        annotationName,
                        element.getSimpleName(),
                        System.lineSeparator(),
                        annotationName
                ));
            }

            // if empty name use null
            String mappedTo = mapping.mappedTo();
            mappedTo = mappedTo.equals("") ? null : mappedTo;

            // if collection use first argument
            if (type.isCollection() && type.getArguments()[0] instanceof RealTypeModel) {
                type = type.getArguments()[0];
            }

            for (String view : mapping.views()) {
                String oneWayTarget = null;
                if (mapping.useOneWay()) {
                    OneToMany oneToMany = AnnotationUtils.getAnnotation(element, OneToMany.class);
                    oneWayTarget = oneToMany.mappedBy();
                }
                viewMappings.put(view, new ViewContext(mappedTo, type, mapping.useRef(), mapping.useOneWay(), oneWayTarget));
            }
        }

        return viewMappings;
    }

    private String[] parseModifiers(Set<Modifier> modifiers) {
        List<String> modelModifiers = new ArrayList<>();

        if (modifiers.contains(Modifier.PRIVATE)) {
            modelModifiers.add("private");
        } else if (modifiers.contains(Modifier.PROTECTED)) {
            modelModifiers.add("protected");
        } else if (modifiers.contains(Modifier.PUBLIC)) {
            modelModifiers.add("public");
        }
        if (modifiers.contains(Modifier.STATIC)) {
            modelModifiers.add("static");
        }
        if (modifiers.contains(Modifier.FINAL)) {
            modelModifiers.add("final");
        }
        if (modifiers.contains(Modifier.ABSTRACT)) {
            modelModifiers.add("abstract");
        }

        return modelModifiers.toArray(String[]::new);
    }

    private ImportModel[] extractImports(Element element) {
        TreePath path = trees.getPath(element);
        CompilationUnitTree compilationUnit = path.getCompilationUnit();

        return compilationUnit.
                getImports().
                stream().
                map(im -> (JCTree.JCImport) im).
                map(im -> {
                    JCTree qualifiedIdentifier = im.getQualifiedIdentifier();
                    boolean isStatic = im.isStatic();
                    boolean isWildcard = TreeInfo.name(qualifiedIdentifier).toString().equals("*");

                    boolean isAnnotation = !isWildcard && qualifiedIdentifier.type != null && (qualifiedIdentifier.type.tsym.flags() & Flags.ANNOTATION) != 0;
                    return new ImportModel(qualifiedIdentifier.toString(), isStatic, isAnnotation);
                }).
                toArray(ImportModel[]::new);
    }

    private JCTree.JCVariableDecl getVariableDecl(Symbol.VarSymbol symbol) {
        return (JCTree.JCVariableDecl) elementUtils.getTree(symbol);
    }

    private JCTree.JCClassDecl getClassDecl(Symbol.ClassSymbol symbol) {
        return (JCTree.JCClassDecl) elementUtils.getTree(symbol);
    }

    private JCTree.JCMethodDecl getMethodDecl(Symbol.MethodSymbol symbol) {
        return (JCTree.JCMethodDecl) elementUtils.getTree(symbol);
    }
}
