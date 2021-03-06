package dk.dbc.search.solrdocstore;

import org.junit.Test;

import javax.persistence.EntityManager;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class QueueRuleEntityIT extends JpaSolrDocStoreIntegrationTester {

    @Test
    public void StoreEntity() {
        EntityManager em = env().getEntityManager();
        env().getPersistenceContext().run(() -> {
            em.persist(new QueueRuleEntity("foo"));
        });

        QueueRuleEntity qr = env().getPersistenceContext()
                .run(() -> em.find(QueueRuleEntity.class, "foo"));

        assertThat(qr.getQueue(), is("foo"));
    }

    @Test
    public void LoadEntity() {
        executeScriptResource("/queueEntityTestData.sql");
        EntityManager em = env().getEntityManager();

        QueueRuleEntity qr = env().getPersistenceContext()
                .run(() -> em.find(QueueRuleEntity.class, "bar"));

        assertThat(qr.getQueue(), is("bar"));
    }
}
