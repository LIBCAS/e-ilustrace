package cz.inqool.eas.common.reporting.input;

import cz.inqool.eas.common.domain.index.dto.params.Params;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutocompleteReportInputField extends ReportInputField {
    private String autocompleteUrl;
    private String autocompleteLabelField;
    private String autocompleteApiUrl;
    private Params autocompleteParams;
    private boolean multiple;

    public AutocompleteReportInputField(String name, String label, String url, String labelField, boolean multiple, String apiUrl, Params params) {
        super(name, label, ReportInputFieldType.AUTOCOMPLETE);
        this.autocompleteUrl = url;
        this.autocompleteLabelField = labelField;
        this.multiple = multiple;
        this.autocompleteApiUrl = apiUrl;
        this.autocompleteParams = params;
    }
}
