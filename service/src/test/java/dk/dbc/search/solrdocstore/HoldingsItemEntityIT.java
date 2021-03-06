package dk.dbc.search.solrdocstore;

import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HoldingsItemEntityIT extends JpaSolrDocStoreIntegrationTester {

    @Test
    public void StoreEntity() {
        EntityManager em = env().getEntityManager();
        env().getPersistenceContext().run(() -> {

            Map<String, List<String>> doc1 = new HashMap<>();
            doc1.put("titel", Collections.singletonList("unix bogen"));
            doc1.put("id", Collections.singletonList("argle"));

            Map<String, List<String>> doc2 = new HashMap<>();
            doc2.put("titel", Collections.singletonList("unix bogen"));
            doc2.put("id", Collections.singletonList("argle"));
            doc2.put("dyr", Collections.singletonList("hest"));
            em.persist(new HoldingsItemEntity(200, "1234", "v0.1", Arrays.asList(doc1, doc2), ""));
        });

        Object key = new AgencyItemKey(200, "1234");
        HoldingsItemEntity be2 = em.find(HoldingsItemEntity.class, key);

        assertThat(be2.getAgencyId(), is(200));
        assertThat(be2.getBibliographicRecordId(), is("1234"));
    }

    @Test
    public void LoadEntity() throws Exception {

        executeScriptResource("/entityTestData.sql");
        EntityManager em = env().getEntityManager();

        AgencyItemKey key = new AgencyItemKey(300, "4321");
        HoldingsItemEntity be = env().getPersistenceContext().run(() -> em.find(HoldingsItemEntity.class, key));

        assertThat(be.getAgencyId(), is(300));
        assertThat(be.getBibliographicRecordId(), is("4321"));

        assertThat(be.getProducerVersion(), is("revision"));
        ArrayList<Map<String, List<String>>> expected = new ArrayList<>();

        Map<String, List<String>> doc1 = new HashMap<>();
        doc1.put("title", Collections.singletonList("unix bogen"));
        doc1.put("id", Collections.singletonList("argle"));
        expected.add(doc1);

        Map<String, List<String>> doc2 = new HashMap<>();
        doc2.put("title", Collections.singletonList("unix bogen"));
        doc2.put("id", Collections.singletonList("argle"));
        doc2.put("dyr", Collections.singletonList("hest"));
        expected.add(doc2);

        assertThat(be.getIndexKeys(), is(expected));

        assertThat(be.getTrackingId(), is("track"));
    }

}
