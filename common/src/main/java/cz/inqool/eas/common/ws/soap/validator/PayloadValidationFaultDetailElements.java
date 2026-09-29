package cz.inqool.eas.common.ws.soap.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.xml.namespace.QName;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PayloadValidationFaultDetailElements {

    static QName Q_TIMESTAMP      = QName.valueOf("timestamp");
    static QName Q_CODE           = QName.valueOf("code");
    static QName Q_ERROR          = QName.valueOf("error");
    static QName Q_LINE_NUMBER    = QName.valueOf("line");
    static QName Q_COLUMN_NUMBER  = QName.valueOf("column");
    static QName VALIDATION_ERROR = QName.valueOf("VALIDATION_ERROR");
}
