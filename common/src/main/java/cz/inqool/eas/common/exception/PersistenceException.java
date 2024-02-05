package cz.inqool.eas.common.exception;

/**
 * @deprecated use {@link cz.inqool.eas.common.exception.v2.PersistenceException} instead
 */
@Deprecated
public class PersistenceException extends GeneralException {

    public PersistenceException(String message) {
        super(message);
    }

}
