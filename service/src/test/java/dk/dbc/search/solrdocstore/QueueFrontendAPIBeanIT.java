package dk.dbc.search.solrdocstore;

import dk.dbc.commons.jsonb.JSONBContext;
import dk.dbc.commons.jsonb.JSONBException;
import dk.dbc.search.solrdocstore.asyncjob.AsyncJobSessionHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import java.util.List;

public class QueueFrontendAPIBeanIT extends JpaSolrDocStoreIntegrationTester {

    EntityManager em;
    JSONBContext jsonbContext = new JSONBContext();

    QueueFrontendAPIBean bean;

    @Before
    public void before() {
        // Setup bean
        em = env().getEntityManager();
        bean = new QueueFrontendAPIBean();
        bean.entityManager = em;
        bean.sessionHandler = new AsyncJobSessionHandler();
        QueueRulesBean q = new QueueRulesBean();
        q.entityManager = em;
        bean.queueRulesBean = q;

        // Setup records
        executeScriptResource("/queueFrontendIT.sql");
    }

    @Test
    public void testGetQueueRules() {
        Response queueRulesReponse = bean.getQueueRules();
        FrontendReturnListType<QueueRuleEntity> queueRules = (FrontendReturnListType<QueueRuleEntity>) queueRulesReponse.getEntity();
        List<QueueRuleEntity> queueRuleEntityList = queueRules.result;
        int expected = 4;
        Assert.assertEquals(expected, queueRuleEntityList.size());
    }

    @Test
    public void testCreateQueueRule() throws JSONBException {
        QueueRuleEntity createQueueRule = new QueueRuleEntity("q1");
        String queueJson = jsonbContext.marshall(createQueueRule);
        Response createQueueRuleResponse = env().getPersistenceContext()
                .run(() -> bean.createQueueRule(null, queueJson)
                );
        QueueRuleEntity createdQueueRule = (QueueRuleEntity) createQueueRuleResponse.getEntity();
        Assert.assertEquals(createQueueRule, createdQueueRule);
        int expected = 5;
        Assert.assertEquals(expected, getNumberOfQueues());
    }

    @Test(expected = RuntimeException.class)
    public void testCreateInvalidQueueRule() {
        String queueJson = "{not:\"proper\"}";
        Response createQueueRuleResponse = env().getPersistenceContext()
                .run(() -> bean.createQueueRule(null, queueJson));
    }

    @Test
    public void testDeleteQueueRule() throws JSONBException {
        QueueRuleEntity deleteQueueRule = new QueueRuleEntity("queue2");
        Response deleteQueueRuleResponse = env().getPersistenceContext()
                .run(() -> bean.deleteQueueRule("queue2"));
        QueueRuleEntity deletedQueueRule = (QueueRuleEntity) deleteQueueRuleResponse.getEntity();
        Assert.assertEquals(deleteQueueRule, deletedQueueRule);
        int expected = 3;
        Assert.assertEquals(expected, getNumberOfQueues());

    }

    @Test
    public void testDeleteNonExistingQueueRule() {
        Response createQueueRuleResponse = env().getPersistenceContext()
                .run(() -> bean.deleteQueueRule("non-existing"));
        Assert.assertEquals(createQueueRuleResponse.getStatus(), 404);
        int expected = 4;
        Assert.assertEquals(expected, getNumberOfQueues());
    }

    public int getNumberOfQueues() {
        return ( (Number) em.createQuery("SELECT COUNT(q) FROM QueueRuleEntity q").getSingleResult() ).intValue();
    }
}
