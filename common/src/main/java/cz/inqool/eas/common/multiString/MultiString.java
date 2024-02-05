package cz.inqool.eas.common.multiString;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.inqool.eas.common.intl.Language;

import java.util.HashMap;

/**
 * Object for multi language strings
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public class MultiString extends HashMap<Language, String> {
}
