package cz.inqool.eas.common.state.app;

import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainViews;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;


@DomainViews
@Setter
@Getter
@Entity
@Table(name = "eas_app_state")
public class AppState extends DatedObject<AppState> {
    /**
     * JSON state
     */
    protected String state;
}
