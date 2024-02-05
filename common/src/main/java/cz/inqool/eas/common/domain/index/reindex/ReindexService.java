package cz.inqool.eas.common.domain.index.reindex;

import cz.inqool.eas.common.domain.DomainRepository;
import cz.inqool.eas.common.utils.AopUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class ReindexService {
    private List<DomainRepository<?, ?, ?, ?, ?>> repositories;

    public List<Class<?>> getRepositories() {
        return repositories.stream()
                .map(Object::getClass)
                .sorted(Comparator.comparing(Class::getSimpleName))
                .collect(Collectors.toList());

    }

    @Transactional
    public void reindex(List<String> repositoryClasses) {
        List<DomainRepository<?, ?, ?, ?, ?>> repositories = this.repositories.stream()
                .filter(repository -> repositoryClasses == null || repositoryClasses.isEmpty() || repositoryClasses.contains(repository.getClass().getName()))
                .collect(Collectors.toList());

        int counter = 0;
        int total = repositories.size();
        for (var repository : repositories) {
            log.info("Reindexing repository {}/{}: {}", ++counter, total, repository);

            if (!repository.isIndexManaged()) {
                log.info("Skipping, because repository does not have managed index.");
                continue;
            }

            if (repository.isIndexInitialized()) {
                log.info("Found existing index {}.", repository);
                log.info("Dropping existing index.");
                repository.dropIndex();
            }

            log.info("Indexing {}.", repository);
            repository.reindex();
        }

        log.info("Reindexing complete");
    }

    @Autowired(required = false)
    public void setRepositories(List<DomainRepository<?, ?, ?, ?, ?>> repositories) {
        this.repositories = repositories.stream()
                .map(AopUtils::unwrap)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
