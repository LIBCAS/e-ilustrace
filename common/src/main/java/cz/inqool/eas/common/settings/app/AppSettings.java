package cz.inqool.eas.common.settings.app;

import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainViews;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@DomainViews
@Setter
@Getter
@Entity
@Table(name = "eas_app_settings")
public class AppSettings extends DatedObject<AppSettings> {
    /**
     * JSON settings
     */
    protected String settings;
}
