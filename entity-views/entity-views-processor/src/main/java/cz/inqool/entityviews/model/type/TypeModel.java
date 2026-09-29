package cz.inqool.entityviews.model.type;

public interface TypeModel {
    String getDefinition();
    String getUsage();

    String getArgumentsDefinition();
    String getArgumentsUsage();

    String getFullName();
    String getFullViewName();
    String getRefType();
    TypeModel[] getArguments();

    boolean isUsedView();
    boolean isCollection();
    String getCollectionImplementation();
}
