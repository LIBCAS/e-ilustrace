package cz.inqool.eas.common.domain.index.dto.filter.custom;

import cz.inqool.eas.common.domain.index.dto.filter.LogicalFilter;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class is used in {@link CustomFilterModel} as base class for specific parameters.
 */
@Getter
@Setter
abstract public class CustomFilterSpecificParameters {

    /**
     * This field indicates whether all parameters in sub class should be used as OR or AND
     */
    protected CustomFilterOperation customFilterOperation = CustomFilterOperation.AND;

    abstract public LogicalFilter processSpecificParameters();
}
