package cz.inqool.eas.common.storage.file;

import cz.inqool.eas.common.authored.store.AuthoredStore;

public class FileStore extends AuthoredStore<File, File, QFile> {

    public FileStore() {
        super(File.class);
    }
}
