package cz.inqool.entityviews.model;

import cz.inqool.entityviews.ViewContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static cz.inqool.entityviews.context.ContextHolder.getContext;
import static cz.inqool.entityviews.function.Printable.println;

@AllArgsConstructor
@Getter
@Setter
public class UnitModel {
    private ImportModel[] imports;

    private ClassModel clazz;

    public String getFileName() {
        ViewContext view = getContext().getView();

        if (view.isUseRef()) {
            return clazz.getType().getFullRefName();
        } else {
            return clazz.getType().getFullViewName();
        }
    }

    public void printUnit() {
        printPackage();
        printImports();

        clazz.printClass();
    }

    public void printRef() {
        printPackage();
        printImports();

        clazz.printRef();
    }

    private void printPackage() {
        String packageName = clazz.getType().getPackageName();

        if (packageName != null) {
            println("package ", packageName, ";");
            println();
        }
    }

    private void printImports() {
        for (ImportModel importModel : imports) {
            if (importModel.isAnnotation()) {
                continue;
            }

            importModel.print();
        }

        if (imports.length > 0) {
            println();
        }
    }
}
