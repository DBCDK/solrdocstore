/*
 * Copyright (C) 2018 DBC A/S (http://dbc.dk/)
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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.dbc.search.solrdocstore.updater.profile.Profile;
import dk.dbc.search.solrdocstore.updater.profile.ProfileServiceBean;
import dk.dbc.vipcore.marshallers.LibraryRules;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.ws.rs.client.Client;

import dk.dbc.vipcore.marshallers.LibraryRulesResponse;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.*;

import static org.hamcrest.MatcherAssert.*;

/**
 *
 * This runs business logic test from the directory
 * src/test/resource/BusinessLogic/test
 * <p>
 * It reads VipCore "responses" from the
 * src/test/resource/BusinessLogic/vipCore directory.
 * <p>
 * A test is located in a directory named after the function to test and an
 * optional dash + description. In the directory 2 files needs to be present:
 * source.json (the json as it comes from solr-doc-store-service), and
 * expected.json.
 * <p>
 * expected.json is an array of strings with the format:
 * <p>
 *  * plus/minus ('+'/'-') - if it is an addition or removal of a field
 *  * underscore colon ('_:') - indicator of a nested document value (you can't
 * distinguish nested documents, order is not guaranteed)
 *  * fieldname colon (xxx':') - the field that has changes
 *  * value - the value that has been added/removed from the field
 * <p>
 * a field should be repeated for each value that is added
 *
 * @author DBC {@literal <dbc.dk>}
 */
@RunWith(Parameterized.class)
public class BusinessLogicTest {

    private static final ObjectMapper O = new ObjectMapper()
            .configure(SerializationFeature.INDENT_OUTPUT, true)
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

    private static final SolrFields MOCK_SOLR_FIELDS = new SolrFields() {
        // know all fields
        @Override
        public boolean isKnownField(String name) {
            return true;
        }
    };

    private static final SolrCollection MOCK_SOLR_COLLECTION = new SolrCollection() {
        @Override
        public SolrFields getSolrFields() {
            return MOCK_SOLR_FIELDS;
        }

        // enable all features
        @Override
        public boolean hasFeature(FeatureSwitch feature) {
            return true;
        }
    };

    private static final ProfileServiceBean MOCK_PROFILE_SERVICE_BEAN = new ProfileServiceBean() {
        @Override
        public Profile getProfile(String agencyId, String profile) {
            switch (agencyId + "-" + profile) {
                case "102030-self":
                    return new Profile(true);
                case "102030-magic":
                    return new Profile(true, "110000-foobar", "220000-katalog");
                case "123456-basic":
                    return new Profile(false, "110000-foobar", "220000-katalog");
                case "876543-self":
                    return new Profile(true);
                default:
                    throw new AssertionError();
            }
        }
    };

    private final String name;
    private final Path testDirectory;
    private final Path vipCore;

    public BusinessLogicTest(String name, Path test, Path vipCore) throws Exception {
        this.name = name;
        this.testDirectory = test;
        this.vipCore = vipCore;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> parameters() throws Exception {
        Path path = new File(BusinessLogicTest.class.getResource("/BusinessLogic").toURI())
                .toPath()
                .toAbsolutePath();
        Path vipCore = path.resolve("vipCore");
        return Arrays.stream(path.resolve("tests").toFile().listFiles(File::isDirectory))
                .map(f -> new Object[] {f.getName(), f.toPath(), vipCore})
                .sorted((l, r) -> ( (String) l[0] ).compareTo((String) r[0]))
                .collect(Collectors.toList());
    }

    @Test
    public void test() throws Exception {
        System.out.println(name);
        System.out.println("test = " + testDirectory);
        System.out.println("vipCore = " + vipCore);
        BusinessLogic businessLogic = new BusinessLogic() {
        };
        businessLogic.oa = new OpenAgency() {
            @Override
            public OpenAgencyLibraryRule libraryRule(String agencyId) {
                try (InputStream is = new FileInputStream(vipCore.resolve(agencyId + ".json").toFile())){
                    return new OpenAgencyLibraryRule(libraryRulesFromInputStream(is));
                } catch (IOException ex) {
                    System.err.println("ex = " + ex);
                    throw new RuntimeException(ex);
                }
            }
        };
        businessLogic.config = new Config("solrUrl=Not-Relevant",
                                          "zookeeperUrl=Not-Relevant",
                                          "profileServiceUrl=Not-Relevant",
                                          "solrDocStoreUrl=Not-Relevant",
                                          "solrAppId=Not-Relevant",
                                          "queues=Not-Relevant",
                                          "openAgencyUrl=Not-Relevant",
                                          "scanProfiles=102030-magic,102030-self,123456-basic,876543-self",
                                          "scanDefaultFields=scan.lti,scan.lfo") {
            @Override
            protected Set<SolrCollection> makeSolrCollections(Client client) throws IllegalArgumentException {
                return Collections.EMPTY_SET;
            }
        };
        businessLogic.profileService = MOCK_PROFILE_SERVICE_BEAN;

        JsonNode json = O.readTree(testDirectory.resolve("source.json").toFile());
        JsonNode expectedJson = O.readTree(testDirectory.resolve("expected.json").toFile());
        String[] expected = StreamSupport.stream(expectedJson.spliterator(), false)
                .map(JsonNode::asText)
                .toArray(String[]::new);

        SolrInputDocument before = MOCK_SOLR_FIELDS.newDocumentFromIndexKeys(json.get("bibliographicRecord").get("indexKeys"));
        businessLogic.addNestedHoldingsDocuments(before, json, MOCK_SOLR_COLLECTION, "repoId");
        try {
            String methodName = name.split("[^0-9a-zA-Z_]", 2)[0];
            callMethodByName(methodName, businessLogic, json, MOCK_SOLR_COLLECTION);
        } catch (InvocationTargetException ex) {
            throw (Exception) ex.getTargetException();
        }
        SolrInputDocument after = MOCK_SOLR_FIELDS.newDocumentFromIndexKeys(json.get("bibliographicRecord").get("indexKeys"));
        businessLogic.addNestedHoldingsDocuments(after, json, MOCK_SOLR_COLLECTION, "repoId");

        List<String> diff = diff(before, after);
        System.out.println("diff = " + diff + "; expected = " + Arrays.asList(expected));

        assertThat(diff, containsInAnyOrder(expected));
    }

    private void callMethodByName(String name, BusinessLogic instance, Object... objectCandidates) throws Exception {
        Function<Class<?>, Object> objectFinder = type -> {
            for (Object obj : objectCandidates) {
                if (type.isAssignableFrom(obj.getClass()))
                    return obj;
            }
            throw new IllegalArgumentException("Cannot generate parameter for type: " + type.getName());
        };
        Method method = Stream.of(BusinessLogic.class.getMethods())
                .filter(m -> m.getName().equals(name))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Cannot find method: " + name));
        Object[] callArgs = Stream.of(method.getParameterTypes())
                .map(objectFinder)
                .toArray();
        method.invoke(instance, callArgs);
    }

    /**
     * Make a list of ['+'/'-']['_:'/''][field][':'][value] from 2 json
     * documents
     *
     * @param before left side document
     * @param after  right side document
     * @return list of changes
     */
    private List<String> diff(SolrInputDocument before, SolrInputDocument after) {
        HashSet<String> beforeKeys = new HashSet<>();
        addDocumentKeysTo("", before, beforeKeys);
        HashSet<String> afterKeys = new HashSet<>();
        addDocumentKeysTo("", after, afterKeys);

        HashSet<String> removed = new HashSet<>(beforeKeys);
        removed.removeAll(afterKeys);
        HashSet<String> added = new HashSet<>(afterKeys);
        added.removeAll(beforeKeys);

        ArrayList<String> ret = new ArrayList<>();
        removed.forEach(s -> ret.add("-" + s));
        added.forEach(s -> ret.add("+" + s));
        return ret;
    }

    private void addDocumentKeysTo(String prefix, SolrInputDocument doc, HashSet<String> keys) {
        doc.forEach((String field, SolrInputField values) -> {
            values.getValues()
                    .forEach(value -> keys.add(prefix + field + ":" + value));
        });
        if (doc.hasChildDocuments()) {
            String nestedPrefix = prefix.isEmpty() ? "_:" : "_" + prefix;
            doc.getChildDocuments()
                    .forEach(child -> addDocumentKeysTo(nestedPrefix, child, keys));
        }
    }
}
