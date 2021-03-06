
DROP TABLE OpenAgencyCache;

CREATE TABLE OpenAgencyCache (
    agencyId NUMERIC(6) NOT NULL,
    libraryType TEXT NOT NULL,
    partOfDanbib BOOLEAN NOT NULL,
    authCreateCommonRecord BOOLEAN NOT NULL,
    fetched TIMESTAMP NOT NULL,
    PRIMARY KEY (agencyId)
);

