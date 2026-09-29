package cz.inqool.eas.eil.mirador.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "@id",
        "@type",
        "format",
        "height",
        "width",
        "service"
})
public class Resource {
    public static final String TYPE = "dctypes:Image";
    public static final String FORMAT = "image/jpeg";
    public static final String ID_SUFFIX = "/full/full/0/default.jpg";

    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private String type = TYPE;

    @JsonProperty("format")
    private final String format = FORMAT;

    @JsonProperty("height")
    private int height;

    @JsonProperty("width")
    private int width;

    @JsonProperty("service")
    private Service service;

    @JsonCreator
    public Resource(String id, int height, int width, Service service) {
        this.id = id + ID_SUFFIX;
        this.height = height;
        this.width = width;
        this.service = service;
    }
}
