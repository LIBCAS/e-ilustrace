package cz.inqool.eas.common.module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Definition of application module.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ModuleDefinition {
    protected String id;
    protected String name;
}
