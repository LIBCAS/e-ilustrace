package cz.inqool.eas.common.domain.index.dto.filter.custom;

import cz.inqool.eas.common.domain.index.dto.filter.AndFilter;
import cz.inqool.eas.common.domain.index.dto.filter.LogicalFilter;
import cz.inqool.eas.common.domain.index.dto.filter.OrFilter;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Class represents linked list where every node of the list is {@link T}
 * and the {@link CustomFilterOperation} (either AND or OR) determine whether this node should be in
 * AndFilter or OrFilter with all previous nodes
 */
@Getter
@Setter
public class CustomFilterModel<T extends CustomFilterSpecificParameters> {

    @NotNull
    private CustomFilterOperation operation;

    @NotNull
    private T specificParameters;

    @Valid
    private CustomFilterModel<T> previousCustomFilterModel;

    /**
     * Method recursively process {@code specificParameters} and return single Logical filter
     */
    public LogicalFilter processSpecificParams() {
        return processRecursively(this);
    }

    private LogicalFilter processRecursively(CustomFilterModel<T> model) {
        if (model.getPreviousCustomFilterModel() == null) {
            return model.getSpecificParameters().processSpecificParameters();
        } else {
            switch (model.getOperation()) {
                case AND:
                    return new AndFilter(model.getSpecificParameters().processSpecificParameters(),
                            processRecursively(model.getPreviousCustomFilterModel()));
                case OR:
                    return new OrFilter(model.getSpecificParameters().processSpecificParameters(),
                            processRecursively(model.getPreviousCustomFilterModel()));
                default:
                    throw new RuntimeException("Unreachable switch statement");
            }
        }
    }
}
