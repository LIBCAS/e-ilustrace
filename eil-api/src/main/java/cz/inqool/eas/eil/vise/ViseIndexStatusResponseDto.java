package cz.inqool.eas.eil.vise;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
        "PNAME",
        "INDEX_STATUS"
})
public class ViseIndexStatusResponseDto {
    @JsonProperty("PNAME")
    private String panme;
    @JsonProperty("INDEX_STATUS")
    private IndexStatus indexStatus;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonPropertyOrder({
            "index_is_ongoing",
            "index_is_done",
            "task_progress"
    })
    public static class IndexStatus {
        @JsonProperty("index_is_ongoing")
        private int indexIsOngoing;
        @JsonProperty("index_is_done")
        private int indexIsDone;
        @JsonProperty("task_progress")
        private List<Task> taskProgress;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonPropertyOrder({
            "name",
            "value",
            "max",
            "message",
            "is_complete",
            "has_started",
            "elapsed_ms"
    })
    public static class Task {
        @JsonProperty("name")
        private String name;
        @JsonProperty("value")
        private int value;
        @JsonProperty("max")
        private int max;
        @JsonProperty("message")
        private String message;
        @JsonProperty("is_complete")
        private int isComplete;
        @JsonProperty("has_started")
        private int hasStarted;
        @JsonProperty("elapsed_ms")
        private int elapsedMs;
    }
}
