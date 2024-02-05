package cz.inqool.eas.common.admin.console.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.inqool.eas.common.script.ScriptType;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.OutputStream;
import java.util.Map;

@Getter
@Setter
public class ExecuteScriptRequest {

    @NotNull
    private String script;

    @NotNull
    private ScriptType scriptType;

    private Boolean useTransaction = false;

    private Map<String, Object> params;

    /**
     * Additional output stream for console log access
     */
    @Hidden
    @JsonIgnore
    private OutputStream outputStream;
}
