package cz.inqool.eas.common.pdfa;

import cz.inqool.eas.common.exception.GeneralException;

/**
 * Class for throwing exceptions when there was a problem with converting to PDFA.
 */
public class PdfaConvertException extends GeneralException {

    public PdfaConvertException(String message) {
        super(message);
    }

    public PdfaConvertException(String message, Throwable cause) {
        super(message, cause);
    }

}
