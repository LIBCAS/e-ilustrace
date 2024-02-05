package cz.inqool.eas.common.admin.console.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecuteScriptResponse {

    private Object result;
    private String error;
    private Long duration;


    public static ExecuteScriptResponse success(Object result, Long duration) {
        ExecuteScriptResponse response = new ExecuteScriptResponse();
        response.setResult(result);
        response.setDuration(duration);
        return response;
    }

    public static ExecuteScriptResponse failure(String error, Long duration) {
        ExecuteScriptResponse response = new ExecuteScriptResponse();
        response.setError(error);
        response.setDuration(duration);
        return response;
    }
}
