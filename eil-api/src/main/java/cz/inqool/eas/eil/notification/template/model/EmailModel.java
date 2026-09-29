package cz.inqool.eas.eil.notification.template.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Template model containing email notification model properties
 *
 */
@Setter
@Getter
@JsonPropertyOrder({
        "applicationName",
        "applicationUrl"
})
public abstract class EmailModel extends CommonModel {

    @NotBlank
    @JsonProperty(defaultValue = "E-ilustrace")
    @JsonPropertyDescription("Název aplikace")
    protected String applicationName;

    @NotBlank
    @JsonProperty(defaultValue = "https://test.e-ilustrace.cz/")
    @JsonPropertyDescription("URL aplikace")
    protected String applicationUrl;
}
