package cz.inqool.entityviews;

import cz.inqool.entityviews.context.Context;
import cz.inqool.entityviews.model.UnitModel;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import static cz.inqool.entityviews.context.ContextHolder.clearContext;
import static cz.inqool.entityviews.context.ContextHolder.setContext;

public class ViewGenerator {
    protected ProcessingEnvironment processingEnv;

    public void init(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    public void generateView(UnitModel unit, String viewName, UnitModel[] units) {

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                PrintWriter writer = new PrintWriter(outputStream);

                ViewContext view = new ViewContext(viewName, unit.getClazz().getType(), false, false, null);
                setContext(new Context(view, 0, writer, units, unit));
                unit.printUnit();

                writer.close();

                String qualifiedName = unit.getFileName();
                JavaFileObject viewFile = processingEnv.getFiler().createSourceFile(qualifiedName);

                try (OutputStream fileStream = viewFile.openOutputStream()) {
                    fileStream.write(outputStream.toByteArray());
                }

            } finally {
                clearContext();
            }


        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Failed to create Entity View file: " + unit.getClazz().getType().getClassName());
        }
    }

    public void generateRef(UnitModel unit) {

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                PrintWriter writer = new PrintWriter(outputStream);

                ViewContext view = new ViewContext(null, unit.getClazz().getType(), true, false, null);
                setContext(new Context(view, 0, writer, null, unit));
                unit.printRef();

                writer.close();

                String qualifiedName = unit.getFileName();
                JavaFileObject refFile = processingEnv.getFiler().createSourceFile(qualifiedName);

                try (OutputStream fileStream = refFile.openOutputStream()) {
                    fileStream.write(outputStream.toByteArray());
                }

            } finally {
                clearContext();
            }


        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Failed to create Entity View file: " + unit.getClazz().getType().getClassName());
        }


    }
}
