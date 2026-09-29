package cz.inqool.entityviews.context;

import cz.inqool.entityviews.ViewContext;
import cz.inqool.entityviews.model.UnitModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintWriter;

@AllArgsConstructor
@Getter
@Setter
public class Context {
    private ViewContext view;

    private int tabs;

    PrintWriter out;
    UnitModel[] units;

    UnitModel unit;
}
