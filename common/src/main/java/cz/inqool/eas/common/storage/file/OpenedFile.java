package cz.inqool.eas.common.storage.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;

@AllArgsConstructor
@Getter
public class OpenedFile {
    private final File descriptor;
    private final InputStream stream;
}
