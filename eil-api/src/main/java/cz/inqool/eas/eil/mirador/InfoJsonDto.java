package cz.inqool.eas.eil.mirador;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({
        "@context",
        "@id",
        "protocol",
        "width",
        "height",
})
public class InfoJsonDto {
    @JsonProperty("@context")
    private String context;
    @JsonProperty("@id")
    private String id;
    @JsonProperty("protocol")
    private String protocol;
    @JsonProperty("width")
    private int width;
    @JsonProperty("height")
    private int height;
}
