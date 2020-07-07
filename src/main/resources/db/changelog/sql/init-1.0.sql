INSERT INTO BANKS
VALUES ('2ebf6b62-069b-496b-89f1-c4b4b08e04ae', 'Lion', 'Lion', 'Lion Bank DFSP1 Ltd.', '/images/bank/lion.svg',
        'https://api.lion.mifos.io:8243/token', 'https://api.lion.mifos.io:8243/open-banking/v3.1/aisp/v3.1.2',
        'VA4pKlT3744B2JJp9Hm2PtJPJ3Ea', 'fhytTf0EVSsh4vja1ocbXV1BQwca',
        'http://api.lion.mifos.io/customer/banks/authorize',
        'acefintech', 'https://api.lion.mifos.io:8243/authorize', 'https://api.lion.mifos.io:8243/open-banking/v3.1/pisp/v3.1.2');
INSERT INTO BANKS
VALUES ('a0b5fdcc-a9c2-495c-be0f-7d32603bc453', 'Elephant', 'Elephant', 'Elephant Bank DFSP2 Ltd.', '/images/bank/elephant.svg',
        'https://api.elephant.mifos.io:8243/token', 'https://api.elephant.mifos.io:8243/open-banking/v3.1/aisp/v3.1.2',
        'FiF6RlpHGntTj0ITgS3lYSwbJVsa', 'MSKhzbf3HXnvMMt0psMXuoQiD0Ea',
        'http://api.lion.mifos.io/customer/banks/authorize',
        'acefintech', 'https://api.lion.mifos.io:8243/authorize', 'https://api.lion.mifos.io:8243/open-banking/v3.1/pisp/v3.1.2');
INSERT INTO USERS
VALUES ('tppuser', '{bcrypt}$2a$10$FgRPdjDFcfxrCzWzVO/mZuWwVEm8CxqRNx4qQAOGVjzh/983lUPJy', TRUE);
INSERT INTO AUTHORITIES
VALUES ('tppuser', 'ROLE_USER');
