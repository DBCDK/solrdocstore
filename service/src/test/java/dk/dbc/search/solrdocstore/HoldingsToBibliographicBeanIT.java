package dk.dbc.search.solrdocstore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class HoldingsToBibliographicBeanIT extends JpaSolrDocStoreIntegrationTester {

    private HoldingsToBibliographicBean bean;
    private EntityManager em;

    @Before
    public void before(){
        em = env().getEntityManager();
        bean = new HoldingsToBibliographicBean();
        bean.entityManager = em;
        bean.libraryConfig = new LibraryConfig();
    }

    @Test
    public void updatesIfExists(){
        int agencyId = 132;
        String bibliographicRecordId = "ABC";

        bean.libraryConfig = mockToReturn(LibraryConfig.LibraryType.FBS);
        createBibRecord(agencyId, bibliographicRecordId);
        createH2BRecord(agencyId, bibliographicRecordId, 132);
        bean.tryToAttachToBibliographicRecord(agencyId, bibliographicRecordId);
        HoldingsToBibliographicEntity abc = fetchH2BRecord(agencyId, bibliographicRecordId);
        Assert.assertNotNull(abc);
        assertEquals(132,abc.bibliographicAgencyId);
    }

    @Test
    public void createsNewIfNeeded(){
        int agencyId = 132;
        String bibliographicRecordId = "ABC";

        bean.libraryConfig = mockToReturn(LibraryConfig.LibraryType.FBS);
        createBibRecord(agencyId, bibliographicRecordId);
        bean.tryToAttachToBibliographicRecord(agencyId, bibliographicRecordId);
        HoldingsToBibliographicEntity h2BRecord = fetchH2BRecord(agencyId, bibliographicRecordId);
        Assert.assertNotNull(h2BRecord);
        assertEquals(132,h2BRecord.bibliographicAgencyId);
    }

    @Test
    public void ignoresDeletedBibRecords(){
        int agencyId = 132;
        String bibliographicRecordId = "ABC";

        bean.libraryConfig = mockToReturn(LibraryConfig.LibraryType.FBS);
        createBibRecord(agencyId,bibliographicRecordId);
        deleteBibRecord(agencyId,bibliographicRecordId);
        bean.tryToAttachToBibliographicRecord(agencyId,bibliographicRecordId);
        Assert.assertNull( fetchH2BRecord(agencyId,bibliographicRecordId) );
    }

    @Test
    public void onFBSTest2Levels(){
        int agencyId = 132;
        String bibliographicRecordId = "ABC";

        bean.libraryConfig = mockToReturn(LibraryConfig.LibraryType.FBS);

        createBibRecord(LibraryConfig.COMMON_AGENCY, bibliographicRecordId);
        bean.tryToAttachToBibliographicRecord(agencyId, bibliographicRecordId);
        HoldingsToBibliographicEntity e = fetchH2BRecord(agencyId, bibliographicRecordId);
        Assert.assertNotNull(e);
        assertEquals(LibraryConfig.COMMON_AGENCY,e.bibliographicAgencyId);
    }

    @Test
    public void onFBCSchoolTest3Levels(){
        int agencyId = 132;
        String bibliographicRecordId = "ABC";

        bean.libraryConfig = mockToReturn(LibraryConfig.LibraryType.FBSSchool);
        createBibRecord(LibraryConfig.SCHOOL_COMMON_AGENCY,bibliographicRecordId);
        bean.tryToAttachToBibliographicRecord(agencyId,bibliographicRecordId);
        HoldingsToBibliographicEntity e = fetchH2BRecord(agencyId,bibliographicRecordId);
        Assert.assertNotNull(e);
        assertEquals(LibraryConfig.SCHOOL_COMMON_AGENCY,e.bibliographicAgencyId);
    }

    @Test
    public void failingToAttachIsNoError(){
        int agencyId = 132;
        String bibliographicRecordId = "ABC";

        bean.libraryConfig = mockToReturn(LibraryConfig.LibraryType.FBSSchool);
        bean.tryToAttachToBibliographicRecord(agencyId,bibliographicRecordId);
        HoldingsToBibliographicEntity e = fetchH2BRecord(agencyId,bibliographicRecordId);
        Assert.assertNull(e);
    }

    @Test
    public void onFBSwillReadB2B(){
        int agencyId = 132;
        String bibliographicRecordId = "ABC";
        String newRecordId="DEF";

        bean.libraryConfig = mockToReturn(LibraryConfig.LibraryType.FBS);

        createBibRecord(LibraryConfig.COMMON_AGENCY, bibliographicRecordId);
        createBibRecord(agencyId,newRecordId);
        createB2B(bibliographicRecordId,newRecordId);
        bean.tryToAttachToBibliographicRecord(agencyId, bibliographicRecordId);
        HoldingsToBibliographicEntity e = fetchH2BRecord(agencyId, bibliographicRecordId);
        Assert.assertNotNull(e);
        assertEquals(agencyId,e.bibliographicAgencyId);
        assertEquals(newRecordId,e.bibliographicRecordId);
    }

    @Test
    public void onFBCSchoolWillReadB2B(){
        int agencyId = 132;
        String bibliographicRecordId = "ABC";
        String newRecordId="DEF";

        bean.libraryConfig = mockToReturn(LibraryConfig.LibraryType.FBSSchool);
        createBibRecord(LibraryConfig.SCHOOL_COMMON_AGENCY,bibliographicRecordId);
        createBibRecord(LibraryConfig.COMMON_AGENCY,newRecordId);
        createB2B(bibliographicRecordId,newRecordId);
        bean.tryToAttachToBibliographicRecord(agencyId,bibliographicRecordId);
        HoldingsToBibliographicEntity e = fetchH2BRecord(agencyId,bibliographicRecordId);
        Assert.assertNotNull(e);
        assertEquals(LibraryConfig.COMMON_AGENCY,e.bibliographicAgencyId);
        assertEquals(newRecordId,e.bibliographicRecordId);
    }

    @Test
    public void onB2BUpdateRecalc(){
        bean.libraryConfig = new LibraryConfig();
        bean.libraryConfig.agencyLibraryTypeBean=new AgencyLibraryTypeBean();
        bean.libraryConfig.agencyLibraryTypeBean.entityManager = em;
        int[] agencies = { 4711, 300000, 870970 };
        String[] recordIds = { "A","B"};
        for ( int a: agencies) {
            for(String r : recordIds){
                createBibRecord(a,r);
            }
        }
        createH2BRecord(4711,"A", LibraryConfig.COMMON_AGENCY,"A");
        createH2BRecord( 3711,"A", LibraryConfig.SCHOOL_COMMON_AGENCY,"A");
        createH2BRecord( 4712, "A", 4712,"A");
        createB2B("A","B");
        createAgency(3711, LibraryConfig.LibraryType.FBSSchool);
        createAgency(4711, LibraryConfig.LibraryType.FBS);
        createAgency(4712, LibraryConfig.LibraryType.NonFBS);

        bean.recalcAttachments("B", new HashSet<>(  Arrays.asList(new String[]{ "A" })) );

        assertH2B(4711,"A",LibraryConfig.COMMON_AGENCY,"B");
        assertH2B(3711,"A",LibraryConfig.SCHOOL_COMMON_AGENCY,"B");
        assertH2B(4712,"A",4712,"A");
    }
    private void assertH2B(
            int holdingsAgencyId,
            String holdingsBibliographicRecordId,
            int bibliographicAgencyId,
            String bibliographicRecordId){
        HoldingsToBibliographicEntity e = fetchH2BRecord(holdingsAgencyId, holdingsBibliographicRecordId);
        assertEquals(e.toString(),bibliographicAgencyId, e.bibliographicAgencyId);
        assertEquals(e.toString(),bibliographicRecordId, e.bibliographicRecordId);
    }

    @Test
    public void onNonFBSWillIgnoreB2B(){
        int agencyId = 132;
        String bibliographicRecordId = "ABC";
        String newRecordId="DEF";

        bean.libraryConfig = mockToReturn(LibraryConfig.LibraryType.NonFBS);
        createBibRecord(agencyId,bibliographicRecordId);
        createBibRecord(LibraryConfig.COMMON_AGENCY,bibliographicRecordId);
        createBibRecord(LibraryConfig.COMMON_AGENCY,newRecordId);
        createB2B(bibliographicRecordId,newRecordId);
        bean.tryToAttachToBibliographicRecord(agencyId,bibliographicRecordId);
        HoldingsToBibliographicEntity e = fetchH2BRecord(agencyId,bibliographicRecordId);
        Assert.assertNotNull(e);
        assertEquals(agencyId,e.bibliographicAgencyId);
        assertEquals(bibliographicRecordId,e.bibliographicRecordId);
    }

    private void createB2B(String oldRecordId, String newRecordId) {
        BibliographicToBibliographicEntity e = new BibliographicToBibliographicEntity();
        e.decommissionedRecordId = oldRecordId;
        e.currentRecordId = newRecordId;
        env().getPersistenceContext().run( () -> {
            em.merge(e);
        });
    }

    private LibraryConfig mockToReturn(LibraryConfig.LibraryType libraryType){
        LibraryConfig mock = Mockito.mock(LibraryConfig.class);
        Mockito.when(mock.getLibraryType(Mockito.anyInt())).thenReturn(libraryType);

        return mock;

    }

    private HoldingsToBibliographicEntity fetchH2BRecord(int agencyId, String bibliographicRecordId){
        return env().getPersistenceContext().run( () ->
            em.find(HoldingsToBibliographicEntity.class,
                    new HoldingsToBibliographicKey()
                            .withHoldingAgencyId(agencyId)
                            .withHoldingsBibliographicRecordId(bibliographicRecordId))
        );
    }
    private void deleteBibRecord(int agencyId, String bibliographicRecordId){
        BibliographicEntity e = env().getPersistenceContext().run(() ->
                em.find(BibliographicEntity.class, new AgencyItemKey().withAgencyId(agencyId).withBibliographicRecordId(bibliographicRecordId)));
        e.deleted = true;
        em.merge(e);
    }
    private void createBibRecord(int agencyId, String bibliographicRecordId) {
        env().getPersistenceContext().run(() -> {
            BibliographicEntity e = new BibliographicEntity();
            e.bibliographicRecordId = bibliographicRecordId;
            e.agencyId = agencyId;
            e.work = "{}";
            e.unit = "{}";
            e.deleted=false;
            e.trackingId = "dummy";
            em.merge(e);
        });
    }

    private void createH2BRecord(int holdingsAgencyId, String holdingsBibliographicRecordId, int bibliographicAgencyId) {
        createH2BRecord(holdingsAgencyId,holdingsBibliographicRecordId,bibliographicAgencyId,holdingsBibliographicRecordId);
    }

    private void createH2BRecord(
            int holdingsAgencyId,
            String holdingsBibliographicRecordId,
            int bibliographicAgencyId,
            String bibliographicRecordId) {

        env().getPersistenceContext().run( () -> {
           HoldingsToBibliographicEntity e = new HoldingsToBibliographicEntity(
                   holdingsAgencyId,
                   holdingsBibliographicRecordId,
                   bibliographicAgencyId,
                   bibliographicRecordId
                   );
           em.merge(e);
        });
    }

    private void createAgency(int agencyId, LibraryConfig.LibraryType t){
        env().getPersistenceContext().run( () ->{
            AgencyLibraryTypeEntity e = new AgencyLibraryTypeEntity();
            e.libraryType = t.name();
            e.agencyId = agencyId;
            em.merge(e);
        });

    }

}
