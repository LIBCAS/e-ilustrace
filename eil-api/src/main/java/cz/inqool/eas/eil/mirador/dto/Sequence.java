package cz.inqool.eas.eil.mirador.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({
        "@id",
        "@type",
        "canvases",
})
public class Sequence {
    public static final String TYPE = "sc:Sequence";
    public static final String ID_SUFFIX = "/sequence/normal";

    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private String type = TYPE;

    @JsonProperty("canvases")
    private List<Canvas> canvases;

    @JsonCreator
    public Sequence(String id, List<Canvas> canvases) {
        this.id = id + ID_SUFFIX;
        this.canvases = canvases;
    }
}
