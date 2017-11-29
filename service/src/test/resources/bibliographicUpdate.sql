--- 4 items for different levels of records

INSERT INTO bibliographicSolrKeys (AGENCYID, BIBLIOGRAPHICRECORDID, DELETED, INDEXKEYS, PRODUCERVERSION, TRACKINGID, UNIT, WORK)
VALUES (870970, 'has970', FALSE, '{}' :: JSONB, '5544', 'track', 'unit:3', 'work:3');


INSERT INTO bibliographicSolrKeys (AGENCYID, BIBLIOGRAPHICRECORDID, DELETED, INDEXKEYS, PRODUCERVERSION, TRACKINGID, UNIT, WORK)
VALUES (870970, 'has300', FALSE, '{}' :: JSONB, '5544', 'track', 'unit:3', 'work:3');
INSERT INTO bibliographicSolrKeys (AGENCYID, BIBLIOGRAPHICRECORDID, DELETED, INDEXKEYS, PRODUCERVERSION, TRACKINGID, UNIT, WORK)
VALUES (300000, 'has300', FALSE, '{}' :: JSONB, '5544', 'track', 'unit:3', 'work:3');


INSERT INTO bibliographicSolrKeys (AGENCYID, BIBLIOGRAPHICRECORDID, DELETED, INDEXKEYS, PRODUCERVERSION, TRACKINGID, UNIT, WORK)
VALUES (870970, 'has700', FALSE, '{}' :: JSONB, '5544', 'track', 'unit:3', 'work:3');
INSERT INTO bibliographicSolrKeys (AGENCYID, BIBLIOGRAPHICRECORDID, DELETED, INDEXKEYS, PRODUCERVERSION, TRACKINGID, UNIT, WORK)
VALUES (300000, 'has700', FALSE, '{}' :: JSONB, '5544', 'track', 'unit:3', 'work:3');
INSERT INTO bibliographicSolrKeys (AGENCYID, BIBLIOGRAPHICRECORDID, DELETED, INDEXKEYS, PRODUCERVERSION, TRACKINGID, UNIT, WORK)
VALUES (700000, 'has700', FALSE, '{}' :: JSONB, '5544', 'track', 'unit:3', 'work:3');


INSERT INTO bibliographicSolrKeys (AGENCYID, BIBLIOGRAPHICRECORDID, DELETED, INDEXKEYS, PRODUCERVERSION, TRACKINGID, UNIT, WORK)
VALUES (870970, 'has700no300', FALSE, '{}' :: JSONB, '5544', 'track', 'unit:3', 'work:3');
INSERT INTO bibliographicSolrKeys (AGENCYID, BIBLIOGRAPHICRECORDID, DELETED, INDEXKEYS, PRODUCERVERSION, TRACKINGID, UNIT, WORK)
VALUES (700000, 'has700no300', FALSE, '{}' :: JSONB, '5544', 'track', 'unit:3', 'work:3');


INSERT INTO bibliographicSolrKeys (AGENCYID, BIBLIOGRAPHICRECORDID, DELETED, INDEXKEYS, PRODUCERVERSION, TRACKINGID, UNIT, WORK)
VALUES (700000, 'single700', FALSE, '{}' :: JSONB, '5544', 'track', 'unit:3', 'work:3');

-- Holdings keys

INSERT INTO holdingsitemssolrkeys (AGENCYID, BIBLIOGRAPHICRECORDID, INDEXKEYS, PRODUCERVERSION, TRACKINGID)
VALUES (700000, 'has970', '[]' :: JSONB, 'revision', 'track');
INSERT INTO holdingsitemssolrkeys (AGENCYID, BIBLIOGRAPHICRECORDID, INDEXKEYS, PRODUCERVERSION, TRACKINGID)
VALUES (700000, 'has300', '[]' :: JSONB, 'revision', 'track');
INSERT INTO holdingsitemssolrkeys (AGENCYID, BIBLIOGRAPHICRECORDID, INDEXKEYS, PRODUCERVERSION, TRACKINGID)
VALUES (700000, 'has700', '[]' :: JSONB, 'revision', 'track');

INSERT INTO holdingsitemssolrkeys (AGENCYID, BIBLIOGRAPHICRECORDID, INDEXKEYS, PRODUCERVERSION, TRACKINGID)
VALUES (700000, 'single700', '[]' :: JSONB, 'revision', 'track');


INSERT INTO holdingsitemssolrkeys (AGENCYID, BIBLIOGRAPHICRECORDID, INDEXKEYS, PRODUCERVERSION, TRACKINGID)
VALUES (700000, 'has700no300', '[]' :: JSONB, 'revision', 'track');

INSERT INTO holdingsitemssolrkeys (AGENCYID, BIBLIOGRAPHICRECORDID, INDEXKEYS, PRODUCERVERSION, TRACKINGID)
VALUES (300100, 'has700no300', '[]' :: JSONB, 'revision', 'track');


INSERT INTO holdingsitemssolrkeys (AGENCYID, BIBLIOGRAPHICRECORDID, INDEXKEYS, PRODUCERVERSION, TRACKINGID)
VALUES (700100, 'has300', '[]' :: JSONB, 'revision', 'track');

INSERT INTO holdingsitemssolrkeys (AGENCYID, BIBLIOGRAPHICRECORDID, INDEXKEYS, PRODUCERVERSION, TRACKINGID)
VALUES (700100, 'has700', '[]' :: JSONB, 'revision', 'track');


INSERT INTO holdingsitemssolrkeys (AGENCYID, BIBLIOGRAPHICRECORDID, INDEXKEYS, PRODUCERVERSION, TRACKINGID)
VALUES (700100, 'has970', '[]' :: JSONB, 'revision', 'track');


INSERT INTO holdingsitemssolrkeys (AGENCYID, BIBLIOGRAPHICRECORDID, INDEXKEYS, PRODUCERVERSION, TRACKINGID)
VALUES (700000, 'new', '[]' :: JSONB, 'revision', 'track');

INSERT INTO holdingsitemssolrkeys (AGENCYID, BIBLIOGRAPHICRECORDID, INDEXKEYS, PRODUCERVERSION, TRACKINGID)
VALUES (700100, 'new', '[]' :: JSONB, 'revision', 'track');

INSERT INTO holdingsitemssolrkeys (AGENCYID, BIBLIOGRAPHICRECORDID, INDEXKEYS, PRODUCERVERSION, TRACKINGID)
VALUES (300100, 'new', '[]' :: JSONB, 'revision', 'track');

INSERT INTO holdingsitemssolrkeys (AGENCYID, BIBLIOGRAPHICRECORDID, INDEXKEYS, PRODUCERVERSION, TRACKINGID)
VALUES (300200, 'new', '[]' :: JSONB, 'revision', 'track');


INSERT INTO holdingsitemssolrkeys (AGENCYID, BIBLIOGRAPHICRECORDID, INDEXKEYS, PRODUCERVERSION, TRACKINGID)
VALUES (800000, 'new', '[]' :: JSONB, 'revision', 'track');
