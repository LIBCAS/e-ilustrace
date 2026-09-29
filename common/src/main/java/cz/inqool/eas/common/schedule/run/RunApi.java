package cz.inqool.eas.common.schedule.run;

import cz.inqool.eas.common.dated.DatedApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Tag(name = "Schedule jobs", description = "Schedule jobs CRUD API")
@ResponseBody
@RequestMapping("${schedule.run.url}")
public class RunApi extends DatedApi<
        Run,
        RunDetail,
        RunList,
        RunCreate,
        RunUpdate,
        RunService> {

    @Override
    public RunDetail create(@Valid RunCreate view) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RunDetail update(String id, @Valid RunUpdate view) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(String id) {
        throw new UnsupportedOperationException();
    }
}
