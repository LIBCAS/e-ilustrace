package cz.inqool.eas.common.schedule.run;

import cz.inqool.eas.common.dated.DatedService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunService extends DatedService<
        Run,
        RunDetail,
        RunList,
        RunCreate,
        RunUpdate,
        RunRepository
        > {
}
