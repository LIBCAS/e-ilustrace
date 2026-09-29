package cz.inqool.eas.eil.record.illustration.xlsx;

import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.eil.iconclass.*;
import cz.inqool.eas.eil.record.*;
import cz.inqool.eas.eil.record.illustration.IconclassThemeState;
import cz.inqool.eas.eil.record.illustration.IllustrationXlsx;
import cz.inqool.eas.eil.record.illustration.xlsx.dto.XlsxIllustrationDto;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import cz.inqool.eas.eil.theme.Theme;
import cz.inqool.eas.eil.theme.ThemeRef;
import cz.inqool.eas.eil.theme.ThemeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

import static cz.inqool.eas.eil.record.illustration.Illustration.ILLUSTRATION;

@Service
@Slf4j
public class XlsxReader {
    private static final String IDENTIFIER_SEPARATOR = "-";
    private static final String SEPARATOR = ";";
    public static final int IDENTIFIER_COLUMN = 16;
    public static final int ICONCLASS_COLUMN = 4;
    public static final int ICONCLASS_STATE_COLUMN = 5;
    public static final int THEMES_COLUMN = 18;

    private ThemeRepository themeRepository;
    private RecordRepository recordRepository;
    private IconclassRepository iconclassRepository;
    private TransactionTemplate transactionTemplate;
    private IconclassService iconclassService;

    private ResourceLoader resourceLoader;

    @Value("${eil.import.xlsx.location}")
    private String location;

    public List<XlsxIllustrationDto> readWorkbook() {
        log.info("Parsing .xlsx file '{}'", location);
        try {
            Resource resource = resourceLoader.getResource(location);
            FileInputStream fis = new FileInputStream(resource.getFile());
            Workbook workbook = new XSSFWorkbook(fis);
            return readSheet(workbook);
        } catch (Exception e) {
            log.error("Parsing of .xlsx file '{}' failed", location, e);
            throw new MissingObject(String.format("Parsing of file '%s' failed", location));
        }
    }

    public List<XlsxIllustrationDto> readSheet(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        List<XlsxIllustrationDto> dtos = new ArrayList<>();

        int i = 0;
        for (Row row : sheet) {
            if (i == 0) {
                i++;
                continue;
            }
            String identifier = parseIdentifierCell(row.getCell(IDENTIFIER_COLUMN));

            if (!identifier.isEmpty()) {
                XlsxIllustrationDto dto = new XlsxIllustrationDto(
                        parseCell(row.getCell(ICONCLASS_COLUMN), SEPARATOR),
                        identifier,
                        parseCell(row.getCell(THEMES_COLUMN), SEPARATOR),
                        parseCell(row.getCell(ICONCLASS_STATE_COLUMN)));
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public Set<String> parseCell(Cell cell, String separator) {
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            double content = cell.getNumericCellValue();
            return Set.of(Double.toString(content));
        }
        String content = cell == null ? "" : cell.getStringCellValue();
        return content.isEmpty() ? Collections.emptySet() : Arrays.stream(content.split(separator)).map(String::trim).collect(Collectors.toSet());
    }

    public String parseIdentifierCell(Cell cell) {
        String content = cell == null ? "" : cell.getStringCellValue();
        return content.split(IDENTIFIER_SEPARATOR)[0].trim();
    }

    public String parseCell(Cell cell) {
        return cell == null ? "" : cell.getStringCellValue().trim();
    }

    public boolean readIllustrations() {
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
        List<XlsxIllustrationDto> dtos = readWorkbook();
        List<IllustrationXlsx> updateBatch = new ArrayList<>();
        Collection<Theme> themes = themeRepository.listAll();
        for (XlsxIllustrationDto dto : dtos) {
            IllustrationXlsx ill = recordRepository.findXlsxByIdentifier(dto.getIdentifier());
            if (ill != null && ill.getType().equals(ILLUSTRATION)) {
                ill.setThemes(new LinkedHashSet<>());
                themes.forEach(theme -> {
                    if (dto.getThemes().contains(theme.getName())) {
                        ill.getThemes().add(ThemeRef.toRef(theme));
                    }
                });
                ill.setThemeState(ill.getThemes().isEmpty() ? IconclassThemeState.UNENRICHED : IconclassThemeState.DONE);
                ill.setIconclass(new LinkedHashSet<>());
                dto.getIconclassCodes().forEach(code -> {
                    code = code.strip();
                    IconclassCategory existing = iconclassRepository.findByCode(code);
                    if (existing != null) {
                        ill.getIconclass().add(IconclassCategoryRef.toRef(existing));
                    } else {
                        IconclassCategoryCreate iconclassCategoryCreate = new IconclassCategoryCreate();
                        iconclassCategoryCreate.setCode(code);
                        try {
                            IconclassCategoryDefault iconclass = transactionTemplate.execute(status ->
                                    iconclassService.create(iconclassCategoryCreate));
                            ill.getIconclass().add(IconclassCategoryRef.toRef(IconclassCategoryDefault.toEntity(iconclass)));
                        } catch (Exception e) {
                            String url = iconclassService.constructUrl(code);
                            log.error("Error while creating Iconclass with code '{}' and url '{}'", code, url);
                        }
                    }
                });
                if (dto.getIconclassState() != null) {
                    if (dto.getIconclassState().equalsIgnoreCase("done"))
                        ill.setIconclassState(IconclassThemeState.DONE);
                    else if (dto.getIconclassState().equalsIgnoreCase("in progress"))
                        ill.setIconclassState(IconclassThemeState.INPROGRESS);
                    else
                        ill.setIconclassState(IconclassThemeState.UNENRICHED);
                }
                updateBatch.add(ill);
            }
            if (updateBatch.size() >= 100) {
                transactionTemplate.executeWithoutResult(status -> {
                    recordRepository.update(updateBatch);
                    updateBatch.clear();
                });
            }
        }
        transactionTemplate.executeWithoutResult(status -> recordRepository.update(updateBatch));
        log.info("Finished import of Illustrations");
        return true;
    }

    @Autowired
    public void setRecordRepository(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Autowired
    public void setThemeRepository(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    @Autowired
    public void setIconclassRepository(IconclassRepository iconclassRepository) {
        this.iconclassRepository = iconclassRepository;
    }

    @Autowired
    public void setIconclassService(IconclassService iconclassService) {
        this.iconclassService = iconclassService;
    }

    @Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
