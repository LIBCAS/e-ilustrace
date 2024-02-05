package cz.inqool.eas.eil.mirador.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "@context",
        "@id",
        "profile",
})
public class Service {
    public static final String CONTEXT = "http://iiif.io/api/image/2/context.json";
    public static final String PROFILE = "http://iiif.io/api/image/2/level2.json";

    @JsonProperty("@context")
    private String context = CONTEXT;

    @JsonProperty("@id")
    private String id;

    @JsonProperty("profile")
    private final String profile = PROFILE;

    @JsonCreator
    public Service(String id) {
        this.id = id;
    }
}
