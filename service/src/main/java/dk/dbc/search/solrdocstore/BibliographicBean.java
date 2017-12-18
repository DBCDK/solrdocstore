package dk.dbc.search.solrdocstore;

import dk.dbc.commons.jsonb.JSONBContext;
import static dk.dbc.search.solrdocstore.LibraryConfig.RecordType.SingleRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashSet;
import java.util.Set;

@Stateless
@Path("bibliographic")
public class BibliographicBean {

    private static final Logger log = LoggerFactory.getLogger(BibliographicBean.class);

    private final JSONBContext jsonbContext = new JSONBContext();

    @Inject
    LibraryConfig libraryConfig;

    @PersistenceContext(unitName = "solrDocumentStore_PU")
    EntityManager entityManager;

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response addBibliographicKeys(@Context UriInfo uriInfo, String jsonContent) throws Exception {

        BibliographicEntityRequest be = jsonbContext.unmarshall(jsonContent, BibliographicEntityRequest.class);
        log.info("AddBibliographicKeys called {}:{}", be.agencyId, be.bibliographicRecordId);

        BibliographicEntity dbbe = entityManager.find(BibliographicEntity.class, new AgencyItemKey(be.agencyId, be.bibliographicRecordId), LockModeType.PESSIMISTIC_WRITE);
        if (dbbe == null) {
            entityManager.merge(be.asBibliographicEntity());
            updateHoldingsToBibliographic(be.agencyId, be.bibliographicRecordId);
        } else {
            throw new IllegalStateException("Missing implementation");
        }

        return Response.ok().entity("{ \"ok\": true }").build();
    }

    /*
     *
     */
    private void updateHoldingsToBibliographic(int agency, String recordId) {
        if (libraryConfig.getRecordType(agency) == SingleRecord) {
            updateHoldingsSingleRecord(agency, recordId);
        } else {
            updateHoldingsForCommonRecords(agency, recordId);
        }
    }

    private void updateHoldingsForCommonRecords(int agency, String recordId) {
        TypedQuery<Integer> query = entityManager.createQuery("SELECT h.agencyId FROM HoldingsItemEntity h  WHERE h.bibliographicRecordId = :bibId", Integer.class);
        query.setParameter("bibId", recordId);

        TypedQuery<Integer> q = entityManager.createQuery("SELECT b.agencyId FROM BibliographicEntity b " +
                "WHERE b.deleted=FALSE AND b.bibliographicRecordId = :recId", Integer.class);

        q.setParameter("recId", recordId);

        Set<Integer> bibRecords = new HashSet<>(q.getResultList());

        for (Integer holdingsAgency : query.getResultList()) {
            switch (libraryConfig.getLibraryType(holdingsAgency)) {
                case NonFBS: // Ignore holdings for Non FBS libraries
                    continue;
                case FBS:
                    if (agency == 300000) continue; // 300000 is only for FBSSchool records
                    if (bibRecords.contains(holdingsAgency)) continue;
                    break;
                case FBSSchool:
                    if (agency == 870970 && bibRecords.contains(300000)) continue;
                    if (bibRecords.contains(holdingsAgency)) continue;
                    break;
            }
            addHoldingsToBibliographic(agency, recordId, holdingsAgency);
        }
    }

    private void updateHoldingsSingleRecord(int agency, String recordId) {
        TypedQuery<Long> query = entityManager.createQuery("SELECT count(h.agencyId) FROM HoldingsItemEntity h  WHERE h.bibliographicRecordId = :bibId and h.agencyId = :agency", Long.class);
        query.setParameter("agency", agency);
        query.setParameter("bibId", recordId);

        if (query.getSingleResult() > 0) {
            addHoldingsToBibliographic(agency, recordId, agency);
        }
    }

    private void addHoldingsToBibliographic(int agency, String recordId, Integer holdingsAgency) {
        HoldingsToBibliographicEntity h2b = new HoldingsToBibliographicEntity(
                holdingsAgency, recordId,agency
        );
        entityManager.merge(h2b);
    }

}