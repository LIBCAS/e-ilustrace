package cz.inqool.eas.eil.mirador.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({
        "@context",
        "@type",
        "@id",
        "label",
        "description",
        "sequences",
})
public class Manifest {
    public static final String TYPE = "sc:Manifest";
    public static final String CONTEXT = "http://iiif.io/api/presentation/2/context.json";
    public static final String MANIFEST = "/manifest.json";

    @JsonProperty("@context")
    private String context = CONTEXT;

    @JsonProperty("@type")
    private String type = TYPE;

    @JsonProperty("@id")
    private String id;

    @JsonProperty("label")
    private String label;

    @JsonProperty("description")
    private String description;

    @JsonProperty("sequences")
    private List<Sequence> sequences;

    @JsonCreator
    public Manifest(String id, String label, String description, List<Sequence> sequences) {
        this.id = id + MANIFEST;
        this.label = label;
        this.description = description;
        this.sequences = sequences;
    }
}
