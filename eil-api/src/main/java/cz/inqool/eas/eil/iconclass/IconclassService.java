package cz.inqool.eas.eil.iconclass;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.inqool.eas.common.domain.DomainService;
import cz.inqool.eas.common.exception.v2.ForbiddenOperation;
import cz.inqool.eas.common.exception.v2.InvalidArgument;
import cz.inqool.eas.eil.iconclass.dto.IconclassCategorySpecificationDto;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static cz.inqool.eas.common.utils.AssertionUtils.isNull;
import static cz.inqool.eas.eil.config.exception.EilExceptionCode.*;

@Slf4j
@Service
public class IconclassService extends DomainService<
        IconclassCategory,
        IconclassCategoryDefault,
        IconclassCategoryDefault,
        IconclassCategoryCreate,
        IconclassCategoryDefault,
        IconclassRepository> {

    private ObjectMapper objectMapper;

    private static final String ICONCLASS_URL_PREFIX = "https://iconclass.org/";
    private static final String ICONCLASS_URL_SUFFIX = ".json";

    @Transactional
    @Override
    public IconclassCategoryDefault create(IconclassCategoryCreate view) {
        isNull(repository.findByCode(view.code), () -> new ForbiddenOperation(ENTITY_ALREADY_EXISTS)
                .details(details -> details
                        .property("code", view.code)
                        .clazz(IconclassCategory.class)));

        IconclassCategoryCreate category = new IconclassCategoryCreate();
        String url = constructIconclassUrl(view.code);
        try {
            IconclassCategorySpecificationDto dto = getIconclassApiResponse(new URL(url + ICONCLASS_URL_SUFFIX));
            if (dto == null) {
                throw new InvalidArgument(ICONCLASS_URL_DOES_NOT_EXIST, "Failed to retrieve " + view.code + " category " +
                        "and corresponding URL '" + url + ICONCLASS_URL_SUFFIX + "' from Iconclass. Iconclass category not created.");
            }
            if (dto.getTxt() != null) {
                category.setName(dto.getTxt().getEn());
            }
            category.setCode(view.code);
            category.setUrl(url);
            return super.create(category);
        } catch (MalformedURLException ex) {
            throw new InvalidArgument(MALFORMED_URL, "Malformed URL " + url + ". " +
                    "Creation of Iconclass category aborted.");
        }
    }

    public IconclassCategory createFromInit(String code, String id) {
        IconclassCategory iconclass = repository.findByCode(code);
        if (iconclass != null) return iconclass;

        iconclass = new IconclassCategory();
        iconclass.setId(id);
        String url = constructIconclassUrl(code);
        try {
            IconclassCategorySpecificationDto dto = getIconclassApiResponse(new URL(url + ICONCLASS_URL_SUFFIX));
            if (dto == null) {
                log.debug("Failed to retrieve {} category and corresponding URL '{}' from Iconclass. Iconclass category not created.", code, url);
                return null;
            }
            if (dto.getTxt() != null) {
                iconclass.setName(dto.getTxt().getEn());
            }
            iconclass.setCode(code);
            iconclass.setUrl(url);
            return iconclass;
        } catch (MalformedURLException ex) {
            log.debug("Malformed URL '{}'. Creation of Iconclass category aborted.", url);
        }
        return null;
    }

    /**
     * Iconclass code needs to be encoded because it usually contains special characters (as '%', '+' etc.)
     * @param code String sent from FE (user input)
     * @return encoded Iconclass code
     */
    private static String constructIconclassUrl(String code) {
        try {
            String encoded = URLEncoder.encode(code, StandardCharsets.UTF_8.toString());
            encoded = encoded.replace("+", "%20");
            return ICONCLASS_URL_PREFIX + encoded;
        } catch (UnsupportedEncodingException ex) {
            throw new ForbiddenOperation(ICONCLASS_API_REQUEST_FAILED,
                    "Wrong charset provided when encoding Iconclass code into valid URL. " +
                            "Creation of Iconclass category aborted.");
        }
    }

    private IconclassCategorySpecificationDto getIconclassApiResponse(URL url) {
        try {
            return objectMapper.readValue(url, IconclassCategorySpecificationDto.class);
        } catch (IOException ex) {
            throw new ForbiddenOperation(ICONCLASS_API_REQUEST_FAILED,
                    "Response from Iconclass API could not be casted into DTO class. " +
                            "Creation of Iconclass category aborted.");
        }
    }

    public String constructUrl(String code) {
        try {
            return constructIconclassUrl(code);
        } catch (ForbiddenOperation e) {
            log.error("Wrong charset provided when encoding Iconclass code into valid URL. " +
                    "Creation of Iconclass category aborted.");
            return "";
        }
    }

    @Override
    protected void preUpdateHook(IconclassCategory object) {
        super.preUpdateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    public void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Autowired
    public void setObjectMapper(ObjectMapper mapper) {
        this.objectMapper = mapper;
    }
}
