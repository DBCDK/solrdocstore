package dk.dbc.search.solrdocstore;

import dk.dbc.commons.jsonb.JSONBContext;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Stateless
@Path("holdings")
public class HoldingsItemBean {

    private static final Logger log = LoggerFactory.getLogger(HoldingsItemBean.class);

    private final JSONBContext jsonbContext = new JSONBContext();

    @Inject
    HoldingsToBibliographicBean h2bBean;

    @Inject
    EnqueueSupplierBean queue;

    @PersistenceContext(unitName = "solrDocumentStore_PU")
    EntityManager entityManager;

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response setHoldingsKeys(@Context UriInfo uriInfo, String jsonContent) throws Exception {

        HoldingsItemEntityRequest hi = jsonbContext.unmarshall(jsonContent, HoldingsItemEntityRequest.class);

        setHoldingsKeys(hi.asHoldingsItemEntity(), Optional.of(hi.commitWithin));

        return Response.ok().entity("{ \"ok\": true }").build();
    }

    public void setHoldingsKeys(HoldingsItemEntity hi, Optional<Integer> commitWithin){
        log.info("Updating holdings for {}:{}", hi.agencyId, hi.bibliographicRecordId);
        entityManager.merge(hi);
        Set<AgencyItemKey> affectedKeys =
            h2bBean.tryToAttachToBibliographicRecord(hi.agencyId, hi.bibliographicRecordId);
        EnqueueAdapter.enqueueAll(queue, affectedKeys,commitWithin);
    }

    public List<HoldingsItemEntity> getRelatedHoldings(String bibliographicRecordId, int bibliographicAgencyId){

        Query query = entityManager.createNativeQuery(
                "select * " +
                        "from holdingsitemssolrkeys  " +
                        "where (agencyid,bibliographicrecordid) " +
                        "IN ( select holdingsagencyid,holdingsbibliographicrecordid " +
                        "FROM holdingstobibliographic h2b " +
                        "where h2b.bibliographicagencyid = ? " +
                        "and h2b.bibliographicrecordid = ?)",
                HoldingsItemEntity.class);
        query.setParameter(1,bibliographicAgencyId);
        query.setParameter(2,bibliographicRecordId);
        return query.getResultList();

    }

}
