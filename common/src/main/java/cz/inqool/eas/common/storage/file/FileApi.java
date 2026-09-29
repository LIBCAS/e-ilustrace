package cz.inqool.eas.common.storage.file;

import com.google.common.net.UrlEscapers;
import cz.inqool.eas.common.exception.ForbiddenObject;
import cz.inqool.eas.common.exception.GeneralException;
import cz.inqool.eas.common.exception.MissingAttribute;
import cz.inqool.eas.common.exception.MissingObject;
import cz.inqool.eas.common.exception.dto.RestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Tag(name = "Files", description = "File upload and access API")
@ResponseBody
@RequestMapping("${storage_file.url}")
public class FileApi {
    @Autowired
    @Setter
    private FileManager manager;

    /**
     * Upload a file.
     * <p>
     * File should be uploaded as multipart/form-data.
     *
     * @param file uploaded file with metadata
     * @return reference to a stored file
     * @throws MissingAttribute if file name in given {@code file} is missing
     * @throws ForbiddenObject  if the file size exceeds allowed size limit
     * @throws GeneralException if any other I/O exception occurs
     */
    @Operation(summary = "Upload a file and return the reference to the stored file.", description = "File should be uploaded as multipart/form-data.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = File.class)))
    @ApiResponse(responseCode = "400", description = "The file name is missing.", content = @Content(schema = @Schema(implementation = RestException.class)))
    @ApiResponse(responseCode = "403", description = "Max file size exceeded.", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public File upload(@Parameter(description = "Provided file with metadata", required = true)
                         @RequestParam("file") MultipartFile file) {

        return manager.upload(file);
    }

    /**
     * Get the content of a file with given ID.
     * <p>
     * Also the {@code Content-Length} and {@code Content-Disposition} HTTP headers are set.
     *
     * @param id ID of file to retrieve
     * @return content of a file in an input stream
     * @throws MissingObject if the file was not found
     */
    @Operation(summary = "Get the content of a upload with given ID.", description = "Returns content of an upload in input stream.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "The file was not found.", content = @Content(schema = @Schema(implementation = RestException.class)))
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> download(@Parameter(description = "ID of file to download", required = true)
                                                        @PathVariable("id") String id) {
        OpenedFile openedFile = manager.open(id);
        File file = openedFile.getDescriptor();
        InputStream stream = openedFile.getStream();

        String name = file.getName().replace(",", "").replace("\"", "").replace("'", "");

        String fileName = "filename=\"" + name + "\"";
        String fileNameAsterisk = "filename*=UTF-8''" + UrlEscapers.urlFragmentEscaper().escape(name);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; " + fileName + "; " + fileNameAsterisk)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.getSize()))
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(new InputStreamResource(stream));
    }

    @Operation(summary = "Discard uploaded file.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = File.class)))
    @ApiResponse(responseCode = "404", description = "The file was not found.", content = @Content(schema = @Schema(implementation = RestException.class)))
    @ApiResponse(responseCode = "403", description = "The file is already permanently stored.", content = @Content(schema = @Schema(implementation = RestException.class)))
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public File discard(@Parameter(description = "Provided upload id", required = true)
                        @PathVariable("id") String id) {
        return manager.discardUpload(id);
    }
}
