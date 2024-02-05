package cz.inqool.eas.common.dated;

import cz.inqool.eas.common.dao.simple.dictionary.SimpleDictionaryEntity;
import cz.inqool.eas.common.dao.simple.dictionary.SimpleDictionaryRepository;
import cz.inqool.eas.common.tstutil.CommonTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DatedRepositoryTest extends CommonTestBase {

    @Autowired
    private SimpleDictionaryRepository repository;

    private SimpleDictionaryEntity dic1;
    private SimpleDictionaryEntity dic2;
    private SimpleDictionaryEntity dic3;


    @BeforeEach
    public void init() {
        dic1 = new SimpleDictionaryEntity();
        dic1.setName("First");

        dic2 = new SimpleDictionaryEntity();
        dic2.setName("Second");

        dic3 = new SimpleDictionaryEntity();
        dic3.setName("Thrid");
    }


    @Test
    @Transactional
    public void deletePermanentlyOne() {
        SimpleDictionaryEntity dic3Deleted = dic3;
        dic3Deleted.setDeleted(Instant.now());

        repository.create(List.of(dic1, dic2, dic3Deleted));

        assertEquals(3, repository.countAll());

        repository.deletePermanently(dic2.getId());

        assertEquals(2, repository.countAll());
        assertNull(repository.find(dic2.getId()));

        repository.deletePermanently(dic3Deleted.getId());

        assertEquals(1, repository.countAll());
        assertNull(repository.find(dic3Deleted.getId()));
    }

    @Test
    @Transactional
    public void deletePermanentlyCollection() {
        SimpleDictionaryEntity dic3Deleted = dic3;
        dic3Deleted.setDeleted(Instant.now());

        repository.create(List.of(dic1, dic2, dic3Deleted));

        assertEquals(3, repository.countAll());

        repository.deletePermanently(Set.of(dic1.getId(), dic3Deleted.getId()));

        assertEquals(1, repository.countAll());
        assertNull(repository.find(dic1.getId()));
        assertNull(repository.find(dic3Deleted.getId()));

        repository.deletePermanently(Set.of(dic2.getId()));

        assertEquals(0, repository.countAll());
        assertNull(repository.find(dic2.getId()));
    }

}
