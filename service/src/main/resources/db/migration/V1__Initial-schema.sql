---
--- dbc-solr-document-store
--- Copyright (C) 2014 Dansk Bibliotekscenter a/s, Tempovej 7-11, DK-2750 Ballerup,
--- Denmark. CVR: 15149043
---
--- This file is part of dbc-holdings-items-access.
---
--- dbc-holdings-items-access is free software: you can redistribute it and/or modify
--- it under the terms of the GNU General Public License as published by
--- the Free Software Foundation, either version 3 of the License, or
--- (at your option) any later version.
---
--- dbc-holdings-items-access is distributed in the hope that it will be useful,
--- but WITHOUT ANY WARRANTY; without even the implied warranty of
--- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--- GNU General Public License for more details.
---
--- You should have received a copy of the GNU General Public License
--- along with dbc-holdings-items-access.  If not, see <http://www.gnu.org/licenses/>.
---

CREATE TABLE bibliographicSolrKeys (
    agencyId NUMERIC(6) NOT NULL,
    bibliographicRecordId TEXT NOT NULL,
    work VARCHAR(50) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    producerVersion VARCHAR(100),
    deleted BOOLEAN NOT NULL,
    indexKeys JSONB,
    trackingId VARCHAR(256) NOT NULL DEFAULT '',
    
    PRIMARY KEY ( agencyId, bibliographicRecordId )
);

CREATE TABLE holdingsItemsSolrKeys (
    agencyId NUMERIC(6) NOT NULL,
    bibliographicRecordId TEXT NOT NULL,
    producerVersion VARCHAR(100),
    indexKeys JSONB,
    trackingId VARCHAR(256) NOT NULL DEFAULT '',

    PRIMARY KEY ( agencyId, bibliographicRecordId )
);

CREATE TABLE holdingsToBibliographic (
    holdingsAgencyId NUMERIC(6) NOT NULL,
    holdingsBibliographicRecordId TEXT NOT NULL,
    bibliographicAgencyId NUMERIC(6) NOT NULL,
    bibliographicRecordId TEXT NOT NULL,

    PRIMARY KEY (holdingsAgencyId, holdingsBibliographicRecordId)
);

CREATE INDEX holdingsToBibliographic_Bibliographic
    ON holdingsToBibliographic (bibliographicRecordId, bibliographicAgencyId);

CREATE TABLE BibliographicToBibliographic (
    deadBibliographicRecordId TEXT NOT NULL CHECK ( deadBibliographicRecordId <> '' ),
    liveBibliographicRecordId TEXT NOT NULL CHECK ( liveBibliographicRecordId <> '' ),
    PRIMARY KEY (deadBibliographicRecordId),
    CONSTRAINT newRecordUnique UNIQUE (deadBibliographicRecordId, liveBibliographicRecordId)
);

CREATE TABLE AgencyLibraryType (
    agencyId NUMERIC(6) NOT NULL,
    libraryType TEXT NOT NULL,
    PRIMARY KEY (agencyId)
);

CREATE INDEX agencyLibraryType_AgencyId on AgencyLibraryType (agencyId);