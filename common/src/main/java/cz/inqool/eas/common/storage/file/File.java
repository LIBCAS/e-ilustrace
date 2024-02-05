package cz.inqool.eas.common.storage.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.entityviews.ViewableProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * File stored on file system.
 */
@DomainViews
@Setter
@Getter
@Entity
@Table(name = "eas_file")
public class File extends AuthoredObject<File> {
    /**
     * Filename.
     */
    @Nationalized
    protected String name;

    /**
     * MIME type.
     */
    protected String contentType;

    /**
     * Size of the file content (in bytes).
     */
    protected Long size;

    /**
     * Level of directory hierarchy in which the current system is stored.
     */
    @JsonIgnore
    @ViewableProperty(views = "default")
    protected Integer level;

    /**
     * Permanently stored file.
     */
    protected boolean permanent;
}
