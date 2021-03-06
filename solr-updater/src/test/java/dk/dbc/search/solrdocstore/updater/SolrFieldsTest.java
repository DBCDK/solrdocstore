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

import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

/**
 *
 * @author DBC {@literal <dbc.dk>}
 */
public class SolrFieldsTest {

    public SolrFieldsTest() {
    }

    @Test
    public void testIsKnownField() throws Exception {
        System.out.println("isKnownField");

        SolrFields solrFields = newSolrFields("schema.xml");

        assertTrue("field", solrFields.isKnownField("_version_"));
        assertTrue("dynamicField", solrFields.isKnownField("facet.fool"));
        assertFalse("Unknown field", solrFields.isKnownField("foo"));
    }

    public static SolrFields newSolrFields(String schemaXmlLocation) throws IOException, SAXException {
        try (InputStream is = SolrFieldsTest.class.getClassLoader().getResourceAsStream(schemaXmlLocation)) {
            return new SolrFields(SolrCollection.docFromStream(is));
        }
    }
}
