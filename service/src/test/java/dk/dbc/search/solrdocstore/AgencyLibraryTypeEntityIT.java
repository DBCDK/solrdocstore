package dk.dbc.search.solrdocstore;

import org.junit.Test;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AgencyLibraryTypeEntityIT extends JpaSolrDocStoreIntegrationTester {

    @Test
    public void insertFindAndDelete() {
        int key = 1234;
        LibraryConfig.LibraryType fbs = LibraryConfig.LibraryType.FBS;

        EntityManager em = env().getEntityManager();

        persist(key, fbs, em);


        AgencyLibraryTypeEntity searchResult = findEntityWithKey(key, em);

        assertEquals(key, searchResult.agencyId);
        assertEquals(fbs, LibraryConfig.LibraryType.valueOf(searchResult.libraryType));

        remove(searchResult, em);

        AgencyLibraryTypeEntity res = findEntityWithKey(key, em);

        assertNull(res);

    }

    private void remove(AgencyLibraryTypeEntity entity, EntityManager em) {
        env().getPersistenceContext().run( () -> em.remove(entity));
    }

    private void persist(int key, LibraryConfig.LibraryType fbs, EntityManager em) {
        env().getPersistenceContext().run(() -> {
            AgencyLibraryTypeEntity e = new AgencyLibraryTypeEntity();
            e.agencyId = key;
            e.libraryType = fbs.name();
            em.persist(e);
        });
    }

    private AgencyLibraryTypeEntity findEntityWithKey(int key, EntityManager em){
        return env().getPersistenceContext().run(() -> em.find(AgencyLibraryTypeEntity.class, key));

    }
}