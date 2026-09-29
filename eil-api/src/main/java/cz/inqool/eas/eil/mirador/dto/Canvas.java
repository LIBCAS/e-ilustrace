package cz.inqool.eas.eil.mirador.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({
        "@id",
        "@type",
        "images",
        "height",
        "width",
        "label"
})
public class Canvas {
    public static final String TYPE = "sc:Canvas";
    public static final String ID_SUFFIX = "/canvas/";

    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private String type = TYPE;

    @JsonProperty("images")
    private List<Image> images;

    @JsonProperty("height")
    private int height;

    @JsonProperty("width")
    private int width;

    @JsonProperty("label")
    private String label;

    @JsonCreator
    public Canvas(String id, List<Image> images, int height, int width, String label, String idOrder) {
        this.id = id + ID_SUFFIX + idOrder;
        this.images = images;
        this.height = height;
        this.width = width;
        this.label = label;
    }
}
