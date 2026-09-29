package cz.inqool.eas.common.exception;

import lombok.Getter;

/**
 * Class for throwing exceptions when there was a virus found in uploaded file
 */
@Getter
public class VirusFoundException extends GeneralException {

    private final String name;

    public VirusFoundException(String name) {
        this.name = name;

    }

    @Override
    public String toString() {
        return "VirusFoundException{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public String getMessage() {
        return toString();
    }
}
