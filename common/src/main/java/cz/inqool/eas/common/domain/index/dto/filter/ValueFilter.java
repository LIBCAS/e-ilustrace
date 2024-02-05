package cz.inqool.eas.common.domain.index.dto.filter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Filter representing a filter condition with given {@link ValueFilter#value}.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
abstract public class ValueFilter extends AbstractFilter {

    /**
     * Value used in comparison
     */
    @NotBlank
    protected String value;


    protected ValueFilter(@NotNull String operation) {
        super(operation);
    }

    protected ValueFilter(@NotNull String operation, @NotBlank String value) {
        super(operation);
        this.value = value;
    }
}
