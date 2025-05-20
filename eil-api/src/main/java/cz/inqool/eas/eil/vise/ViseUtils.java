package cz.inqool.eas.eil.vise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
public class ViseUtils {
    public static final String VISE_PATH_DELETE = "_project_delete";
    public static final String VISE_PATH_CREATE = "_project_create";
    public static final String VISE_PATH_INDEX = "_index_create";
    public static final String VISE_PATH_INDEX_STATUS = "index_status?response_format=json";
    public static final String VISE = "vise/";
    public static final String DELETE_FLAG = "1";
    public static final String CREATE_KEY = "pname";
    public static final String RECORD_REPOSITORY = "cz.inqool.eas.eil.record.RecordRepository";

    private final String baseUrl;

    public void deleteViseProject(String project) {
        sendFormRequest(project, DELETE_FLAG, baseUrl + VISE + VISE_PATH_DELETE);
    }

    public void createViseProject(String project) {
        sendFormRequest(CREATE_KEY, project, baseUrl + VISE + VISE_PATH_CREATE);
    }

    public void startIndexingVise(String project) {
        sendFormRequest(null, null, baseUrl + VISE + project + "/" + VISE_PATH_INDEX);
    }

    public boolean fetchIndexStatusLoop(String project) {
        boolean repeat;
        do {
            try {
                ViseIndexStatusResponseDto dto = fetchIndexStatus(project);
                if (dto == null) {
                    log.debug("Vise did not return a response for index status");
                    return false;
                } else {
                    repeat = dto.getIndexStatus().getIndexIsDone() == 0;
                }
            } catch (Exception e) {
                log.error("Error during indexing Vise project '{}'", project, e);
                return false;
            }
        } while (repeat);

        return true;
    }

    public ViseIndexStatusResponseDto fetchIndexStatus(String project) {
        return sendRequest(baseUrl + VISE + project + "/" + VISE_PATH_INDEX_STATUS);
    }

    private void sendFormRequest(String key, String value, String url) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        if (key != null) {
            requestBody.add(key, value);
        }

        HttpEntity<?> request = new HttpEntity<>(requestBody, headers);

        restTemplate.postForLocation(url, request);
    }

    private ViseIndexStatusResponseDto sendRequest(String url) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<ViseIndexStatusResponseDto> response = restTemplate.getForEntity(url, ViseIndexStatusResponseDto.class);
            return response.getBody();
        } catch (RestClientException e) {
            log.info("Exception occurred during VISE REST call {}.\n Trying again.", e.getMessage());
            if (e.getMessage().contains("502 Proxy Error")) {
                log.info("Trying again.");
                ResponseEntity<ViseIndexStatusResponseDto> response = restTemplate.getForEntity(url, ViseIndexStatusResponseDto.class);
                return response.getBody();
            } else {
                throw e;
            }
        }
    }
}
