/*
 * Copyright (C) 2017 DBC A/S (http://dbc.dk/)
 *
 * This is part of dbc-solr-doc-store-updater
 *
 * dbc-solr-doc-store-updater is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dbc-solr-doc-store-updater is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dbc.search.solrdocstore.updater;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.dbc.search.solrdocstore.queue.QueueJob;
import dk.dbc.search.solrdocstore.updater.profile.OpenAgencyProfile;
import dk.dbc.search.solrdocstore.updater.profile.ProfileServiceBean;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

/**
 *
 * @author DBC {@literal <dbc.dk>}
 */
public class DocProducerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Client CLIENT = ClientBuilder.newClient();

    @Test
    public void test() throws Exception {
        System.out.println("test");

        Config config = new Config("solrUrl=Not-Relevant",
                                   "zookeeperUrl=Not-Relevant",
                                   "profileServiceUrl=Not-Relevant",
                                   "solrDocStoreUrl=Not-Relevant",
                                   "solrAppId=Not-Relevant",
                                   "queues=Not-Relevant",
                                   "openAgencyUrl=Not-Relevant",
                                   "vipCoreEndpoint=Not-Relevant",
                                   "scanProfiles=102030-magic,123456-basic",
                                   "scanDefaultFields=abc,def") {
            @Override
            protected Set<SolrCollection> makeSolrCollections(Client client) throws IllegalArgumentException {
                return Collections.EMPTY_SET;
            }

            @Override
            public Client getClient() {
                return CLIENT;
            }
        };

        DocProducer docProducer = new DocProducer() {
            @Override
            public JsonNode fetchSourceDoc(QueueJob job) throws IOException {
                String file = "DocProducerTest/" + job.getAgencyId() + "-" + job.getBibliographicRecordId() + ".json";
                try (InputStream stream = DocProducerTest.class.getClassLoader().getResourceAsStream(file)) {
                    return OBJECT_MAPPER.readTree(stream);
                }
            }
        };

        docProducer.businessLogic = new BusinessLogic();
        docProducer.businessLogic.oa = new OpenAgency() {
            @Override
            public OpenAgencyLibraryRule libraryRule(String agencyId) {
                return new OpenAgencyLibraryRule(true, true, true, true, false, true);
            }
        };
        docProducer.businessLogic.config = config;

        docProducer.businessLogic.profileService = new ProfileServiceBean() {
            @Override
            public OpenAgencyProfile getOpenAgencyProfile(String agencyId, String profile) {
                switch (agencyId + "-" + profile) {
                    case "102030-magic":
                        return new OpenAgencyProfile(true, "220000-katalog");
                    case "123456-basic":
                        return new OpenAgencyProfile(false, "220000-katalog");
                    default:
                        throw new AssertionError();
                }
            }
        };

        JsonNode node = docProducer.fetchSourceDoc(new QueueJob(300101, "clazzifier", "23645564"));

        System.out.println("node = " + node);

        assertFalse(docProducer.isDeleted(node));
        SolrCollection solrCollection = new SolrCollection() {
            @Override
            public SolrFields getSolrFields() {
                try {
                    return SolrFieldsTest.newSolrFields("schema.xml");
                } catch (IOException | SAXException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public boolean hasFeature(FeatureSwitch feature) {
                return true;
            }
        };

        SolrInputDocument document = docProducer.inputDocument(node, solrCollection);
        System.out.println("document = " + document);
        assertTrue(document.containsKey("dkcclterm.po"));
        assertFalse(document.containsKey("unknown.field"));

        assertEquals(3, document.get("rec.holdingsAgencyId").getValues().size());
        assertTrue(document.get("rec.holdingsAgencyId").getValues().contains("300101"));
        assertTrue(document.get("rec.holdingsAgencyId").getValues().contains("300102"));
        assertTrue(document.get("rec.holdingsAgencyId").getValues().contains("300104"));

        assertTrue(document.get("rec.repositoryId").getValues().contains("300101-katalog:23645564"));

        assertTrue(document.containsKey("dkcclterm.ln"));
        assertTrue(document.get("dkcclterm.ln").getValues().contains("777777"));

        System.out.println("OK");
    }

    @Test
    public void trimTexts() throws Exception {
        JsonNode tree = OBJECT_MAPPER.readTree("{'a':['123','1234567890'],'b':['1234567890','567']}".replaceAll("'", "\""));
        SolrFields.trimIndexFieldsLength((ObjectNode) tree, 5);
        String text = OBJECT_MAPPER.writeValueAsString(tree);
        assertEquals("{'a':['123','12345'],'b':['12345','567']}".replaceAll("'", "\""), text);
    }
}
