package cz.inqool.eas.common.signing.request.dto;

import cz.inqool.eas.common.signing.request.SignContent;
import cz.inqool.eas.common.storage.file.File;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UploadSignedContentDto {
    @NotNull
    private SignContent content;
    @NotNull
    private File signed;
}
