package cz.inqool.eas.eil.xml.iimport;

import cz.inqool.eas.common.exception.dto.RestException;
import cz.inqool.eas.eil.record.RecordRepository;
import cz.inqool.eas.eil.xml.MarcCreateProcessor;
import cz.inqool.eas.eil.xml.MarcUpdateProcessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;

import static cz.inqool.eas.eil.config.swagger.HttpStatus.BAD_REQUEST;
import static cz.inqool.eas.eil.config.swagger.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Import API", description = "Api for import of XML record specifications")
@RestController
@RequestMapping("/import")
public class ImportApi {

    private ImportService service;

    private MarcCreateProcessor marcCreateProcessor;

    private MarcUpdateProcessor marcUpdateProcessor;

    private RecordRepository recordRepository;

    @Value("${eil.import.directory}")
    private String importDir;
    @Value("${eil.import.output.illustration}")
    private String illSubdir;
    @Value("${eil.import.output.book}")
    private String bookSubdir;

    @Operation(summary = "Triggers archive extraction and import of XML records. Test feature")
    @ApiResponse(responseCode = OK, description = "OK")
    @ApiResponse(responseCode = BAD_REQUEST, description = "Wrong project data provided", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestException.class)))
    @PostMapping("/all")
    public void extractAndImportRecords() {
        service.extractAndImportRecords();
    }

    @Operation(summary = "Extract unprocessed archives in docker volume (import directory) to specified output dir that will be created. Test feature")
    @ApiResponse(responseCode = OK, description = "OK")
    @ApiResponse(responseCode = BAD_REQUEST, description = "Wrong project data provided", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestException.class)))
    @PostMapping("/extract/{outputDir}")
    public void extractArchives(@PathVariable String outputDir) {
        service.extractArchivesInto(outputDir);
    }

    @Operation(summary = "Parse extracted illustration records from specified output directory to java entities. Test feature")
    @ApiResponse(responseCode = OK, description = "OK")
    @ApiResponse(responseCode = BAD_REQUEST, description = "Wrong project data provided", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestException.class)))
    @PostMapping("/ill/{outputDir}")
    public void importIlls(@PathVariable String outputDir) {
        service.importIllustrations(outputDir);
    }

    @Operation(summary = "Import one specific illustration XML from docker volume and transform it into Java entity. Test feature")
    @ApiResponse(responseCode = OK, description = "OK")
    @ApiResponse(responseCode = BAD_REQUEST, description = "Wrong project data provided", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestException.class)))
    @PostMapping("/ill/{outputDir}/{fileName}")
    public String importIllXml(@PathVariable String outputDir, @PathVariable String fileName) {
        File file = new File(String.join(File.separator, importDir, outputDir, illSubdir, fileName));
        if (recordRepository.findIdByIdentifier(fileName.substring(0, fileName.lastIndexOf("."))) == null) {
            return service.unmarshallAndParseRecord(file, marcCreateProcessor);
        }
        return service.unmarshallAndParseRecord(file, marcUpdateProcessor);
    }

    @Operation(summary = "Import one specific book XML from docker volume and transform it into Java entity. Test feature")
    @ApiResponse(responseCode = OK, description = "OK")
    @ApiResponse(responseCode = BAD_REQUEST, description = "Wrong project data provided", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestException.class)))
    @PostMapping("/book/{outputDir}/{fileName}")
    public String importBookXml(@PathVariable String outputDir, @PathVariable String fileName) {
        File file = new File(String.join(File.separator, importDir, outputDir, bookSubdir, fileName));
        if (recordRepository.findIdByIdentifier(fileName.substring(0, fileName.lastIndexOf("."))) == null) {
            return service.unmarshallAndParseRecord(file, marcCreateProcessor);
        }
        return service.unmarshallAndParseRecord(file, marcUpdateProcessor);
    }

    @Operation(summary = "Extract unprocessed archives in docker volume (import directory) to specified output dir that will be created. Test feature")
    @ApiResponse(responseCode = OK, description = "OK")
    @ApiResponse(responseCode = BAD_REQUEST, description = "Wrong project data provided", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestException.class)))
    @PostMapping("/extract")
    public void extractArchives(@RequestBody ManualImportDto dto) {
        service.extractArchivesInto(dto.getOutputDir(), dto.getReg());
    }

    @Operation(summary = "Parse extracted illustration records from specified output directory to java entities. Test feature")
    @ApiResponse(responseCode = OK, description = "OK")
    @ApiResponse(responseCode = BAD_REQUEST, description = "Wrong project data provided", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestException.class)))
    @PostMapping("/ill")
    public int importIlls(@RequestBody ManualImportDto dto) {
        return service.importIllustrations(dto.getOutputDir(), dto.getStart(), dto.getEnd());
    }

    @Autowired
    public void setService(ImportService service) {
        this.service = service;
    }

    @Autowired
    public void setMarcCreateProcessor(MarcCreateProcessor marcCreateProcessor) {
        this.marcCreateProcessor = marcCreateProcessor;
    }

    @Autowired
    public void setMarcUpdateProcessor(MarcUpdateProcessor marcUpdateProcessor) {
        this.marcUpdateProcessor = marcUpdateProcessor;
    }
    @Autowired
    public void setRecordRepository(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }
}
