ALTER TABLE OpenAgencyCache ADD COLUMN partOfBibDk BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE OpenAgencyCache ALTER partOfBibDk DROP DEFAULT;
