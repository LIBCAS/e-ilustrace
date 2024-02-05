package cz.inqool.eas.eil.mirador.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "@id",
        "@type",
        "motivation",
        "on",
        "resource",
})
public class Image {
    public static final String TYPE = "oa:Annotation";
    public static final String MOTIVATION = "sc:painting";

    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private String type = TYPE;

    @JsonProperty("motivation")
    private final String motivation = MOTIVATION;

    @JsonProperty("on")
    private String on;

    @JsonProperty("resource")
    private Resource resource;

    @JsonCreator
    public Image(String id, String on, Resource resource) {
        this.id = id;
        this.on = on;
        this.resource = resource;
    }
}
