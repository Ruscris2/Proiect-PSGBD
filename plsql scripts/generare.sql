ALTER TABLE CLIENTI DROP CONSTRAINT fk_abonament_activ;
ALTER TABLE TRANZACTII DROP CONSTRAINT fk_client;
ALTER TABLE TRANZACTII DROP CONSTRAINT fk_angajat;
ALTER TABLE TRANZACTII DROP CONSTRAINT fk_abonament;
ALTER TABLE ANGAJATI DROP CONSTRAINT fk_ghiseu;
ALTER TABLE TRASEE_ABONAMENT DROP CONSTRAINT fk_client_ab;
ALTER TABLE TRASEE_ABONAMENT DROP CONSTRAINT fk_traseu_ab;

-- Creare tabela abonamente
DROP TABLE ABONAMENTE;
CREATE TABLE ABONAMENTE(ID NUMBER(10, 0) PRIMARY KEY,
                        PRET NUMBER(10, 0) NOT NULL,
                        TIP VARCHAR2(64) NOT NULL);

-- Creare tabela clienti
DROP TABLE CLIENTI;
CREATE TABLE CLIENTI(ID NUMBER(10, 0) PRIMARY KEY,
                     NUME VARCHAR2(32) NOT NULL,
                     PRENUME VARCHAR2(32) NOT NULL,
                     CNP VARCHAR2(16) UNIQUE NOT NULL,
                     ABONAMENT_ACTIV NUMBER(10,0) NOT NULL,
                     INCEPUT_ABONAMENT DATE,
                     SFARSIT_ABONAMENT DATE,
                     EMAIL VARCHAR2(64) UNIQUE,
                     CONSTRAINT fk_abonament_activ FOREIGN KEY (abonament_activ) REFERENCES abonamente(id));

-- Creare tabela ghisee
DROP TABLE GHISEE;
CREATE TABLE GHISEE(ID NUMBER(10, 0) PRIMARY KEY,
                    ADRESA VARCHAR2(128) NOT NULL,
                    ORAS VARCHAR2(32) NOT NULL);

-- Creare tabela angajati
DROP TABLE ANGAJATI;
CREATE TABLE ANGAJATI(ID NUMBER(10, 0) PRIMARY KEY,
                      NUME VARCHAR2(32) NOT NULL,
                      PRENUME VARCHAR2(32) NOT NULL,
                      CNP VARCHAR2(16) UNIQUE,
                      ID_GHISEU NUMBER(10, 0),
                      EMAIL VARCHAR2(64) UNIQUE,
                      USERNAME VARCHAR2(64) UNIQUE,
                      PASSWORD VARCHAR2(64) UNIQUE,
                      CONSTRAINT fk_ghiseu FOREIGN KEY (id_ghiseu) REFERENCES ghisee(id));

-- Creare tabela tranzactii
DROP TABLE TRANZACTII;
CREATE TABLE TRANZACTII(ID NUMBER(10, 0) PRIMARY KEY,
                        ID_CLIENT NUMBER(10, 0),
                        ID_ANGAJAT NUMBER(10, 0),
                        ID_ABONAMENT NUMBER(10, 0),
                        DATA_TRANZACTIE DATE NOT NULL,
                        CONSTRAINT fk_client FOREIGN KEY (id_client) REFERENCES clienti(id),
                        CONSTRAINT fk_angajat FOREIGN KEY (id_angajat) REFERENCES angajati(id),
                        CONSTRAINT fk_abonament FOREIGN KEY (id_abonament) REFERENCES abonamente(id));

-- Creare tabela trasee                        
DROP TABLE TRASEE;
CREATE TABLE TRASEE(ID NUMBER(10, 0) PRIMARY KEY,
                    RUTA VARCHAR2(256) NOT NULL);

-- Creare tabela trasee abonament                    
DROP TABLE TRASEE_ABONAMENT;
CREATE TABLE TRASEE_ABONAMENT(ID_CLIENT NUMBER(10, 0),
                              ID_TRASEU NUMBER(10, 0),
                              CONSTRAINT fk_client_ab FOREIGN KEY(id_client) REFERENCES clienti(id),
                              CONSTRAINT fk_traseu_ab FOREIGN KEY(id_traseu) REFERENCES trasee(id));

-- Indecsi
DROP INDEX idx_tranzactii_persoane;
CREATE INDEX idx_tranzactii_persoane ON TRANZACTII(ID_CLIENT);
                              
-- Populare tabela abonamente
INSERT INTO abonamente (id, pret, tip) VALUES (-1, 0, 'Fara abonament');
INSERT INTO abonamente (id, pret, tip) VALUES (1, 10, 'Studenti S');
INSERT INTO abonamente (id, pret, tip) VALUES (2, 18, 'Studenti');
INSERT INTO abonamente (id, pret, tip) VALUES (3, 15, 'Pensionari');
INSERT INTO abonamente (id, pret, tip) VALUES (4, 15, 'Elevi');
INSERT INTO abonamente (id, pret, tip) VALUES (5, 28, 'O linie full');
INSERT INTO abonamente (id, pret, tip) VALUES (6, 14, 'O linie partial');
INSERT INTO abonamente (id, pret, tip) VALUES (7, 34, 'Doua linii full');
INSERT INTO abonamente (id, pret, tip) VALUES (8, 17, 'Doua linii partial');
INSERT INTO abonamente (id, pret, tip) VALUES (9, 58, 'Toate liniile full');
INSERT INTO abonamente (id, pret, tip) VALUES (10, 29, 'Toate liniile partial');

-- Populare tabela clienti folosind tabela users
DECLARE
    v_id int;
    v_nume clienti.nume%TYPE;
    v_prenume clienti.prenume%TYPE;
    v_cnp clienti.cnp%TYPE;
    v_abonament_activ clienti.abonament_activ%TYPE;
    v_inceput_abonament date;
    v_sfarsit_abonament date;
    v_zi int;
    v_luna int;
    v_unic int;
    v_email clienti.email%TYPE;
    
    CURSOR lista_clienti_nume IS
        SELECT REGEXP_SUBSTR(name, '[^ ]+', 1, 2) FROM users WHERE user_role LIKE 'user' AND name NOT LIKE 'temp'
        AND name NOT LIKE 'test';
        
    CURSOR lista_clienti_prenume IS
        SELECT REGEXP_SUBSTR(name, '[^ ]+', 1, 1) FROM users WHERE user_role LIKE 'user' AND name NOT LIKE 'temp'
        AND name NOT LIKE 'test';
BEGIN
    -- Deschidere cursori
    OPEN lista_clienti_nume;
    OPEN lista_clienti_prenume;
    
    -- Obtinere 10000 tuple in tabela clienti
    FOR v_index IN 1 .. 10000 LOOP
    
        -- Obtinere id
        SELECT NVL(MAX(id), 0) INTO v_id FROM clienti;
        v_id := v_id + 1;
        
        -- Obtinere nume si prenume unice
        LOOP
            -- Obtinere nume
            FETCH lista_clienti_nume INTO v_nume;
            -- Daca cursorul a ajuns la sfarsit, repozitionare la inceput
            IF lista_clienti_nume%NOTFOUND THEN
                    CLOSE lista_clienti_nume;
                    OPEN lista_clienti_nume;
            END IF;
            
            LOOP
                -- Obtinere prenume
                FETCH lista_clienti_prenume INTO v_prenume;
                -- Daca cursorul a ajuns la sfarsit repozitionare la inceput si iesire din bucla
                IF lista_clienti_prenume%NOTFOUND THEN
                    CLOSE lista_clienti_prenume;
                    OPEN lista_clienti_prenume;
                    EXIT;
                END IF;
                
                -- Verificare daca nume si prenume sunt unice
                SELECT COUNT(*) INTO v_unic FROM clienti WHERE nume LIKE v_nume AND prenume LIKE v_prenume;
                
                EXIT WHEN v_unic = 0;
            END LOOP;
            
            -- Verificare daca nume si prenume sunt unice
            SELECT COUNT(*) INTO v_unic FROM clienti WHERE nume LIKE v_nume AND prenume LIKE v_prenume;
            
            EXIT WHEN v_unic = 0;
        END LOOP;
        
        -- Obtinere CNP
        LOOP
            v_cnp := FLOOR(DBMS_RANDOM.VALUE(1, 3));
            FOR v_nr IN 1..12 LOOP
                v_cnp := v_cnp || FLOOR(DBMS_RANDOM.VALUE(1, 10));
            END LOOP;
            
            SELECT COUNT(*) INTO v_unic FROM clienti WHERE cnp LIKE v_cnp;
            
            EXIT WHEN v_unic = 0;
        END LOOP;
        
        -- Obtinere abonament activ
        SELECT COUNT(*) INTO v_abonament_activ FROM abonamente;
        v_abonament_activ := FLOOR(DBMS_RANDOM.VALUE(1, v_abonament_activ));
        
        -- Obtinere data inceput abonament
        v_zi := FLOOR(DBMS_RANDOM.VALUE(1, 29));
        v_luna := FLOOR(DBMS_RANDOM.VALUE(1, 13));
        v_inceput_abonament := TO_DATE(v_zi || '-' || v_luna || '-2017', 'DD-MM-YYYY');
        
        -- Obtinere data sfarsit abonament
        v_sfarsit_abonament := v_inceput_abonament + 30;
        
        -- Obitinere email
        v_email := LOWER(v_prenume) || '.' || LOWER(v_nume) || '@gmail.com';
        
        -- Inserare in tabela
        INSERT INTO clienti (id, nume, prenume, cnp, abonament_activ, inceput_abonament, sfarsit_abonament, email) 
            VALUES (v_id, v_nume, v_prenume, v_cnp, v_abonament_activ, v_inceput_abonament, v_sfarsit_abonament, v_email);
    END LOOP;
    
    -- Inchidere cursori
    CLOSE lista_clienti_nume;
    CLOSE lista_clienti_prenume;
END;
/

-- Populare tabela ghisee
INSERT INTO ghisee (id, adresa, oras) VALUES (1, 'Piata Unirii', 'Iasi');
INSERT INTO ghisee (id, adresa, oras) VALUES (2, 'Bulevardul Carol I', 'Iasi');
INSERT INTO ghisee (id, adresa, oras) VALUES (3, 'Strada Cuza Voda', 'Iasi');
INSERT INTO ghisee (id, adresa, oras) VALUES (4, 'Strada Elena Doamna', 'Iasi');
INSERT INTO ghisee (id, adresa, oras) VALUES (5, 'Strada Anastasie Panu', 'Iasi');
INSERT INTO ghisee (id, adresa, oras) VALUES (6, 'Bulevardul Tudor Vladimirescu', 'Iasi');
INSERT INTO ghisee (id, adresa, oras) VALUES (7, 'Bulevardul Tutora', 'Iasi');

-- Generare angajati
INSERT INTO angajati (id, nume, prenume, cnp, id_ghiseu, email, username, password)
          VALUES (1, 'Popescu', 'Ion', '1961111024827', 2, 'pop.i@transport.ro', 'pop.i', 'SDsadda3faUhfUa');
INSERT INTO angajati (id, nume, prenume, cnp, id_ghiseu, email, username, password)
          VALUES (2, 'Ioan', 'Radu', '1971131024623', 4, 'raduu@transport.ro', 'raduu', 'ihunZmldfmQfd83');
INSERT INTO angajati (id, nume, prenume, cnp, id_ghiseu, email, username, password) 
          VALUES (3, 'Popescu', 'Mirela', '2961111024827', 7, 'mirelap@transport.ro', 'mirelap', 'QZAMSD9fdmldsa');
INSERT INTO angajati (id, nume, prenume, cnp, id_ghiseu, email, username, password) 
          VALUES (4, 'Darie', 'Sergiu', '1961212024864', 1, 'sergiu.darie@info.uaic.ro', 'sergiu','sergiu');
INSERT INTO angajati (id, nume, prenume, cnp, id_ghiseu, email, username, password) 
          VALUES (5, 'Ababei', 'Vlad', '1961213024256', 5, 'abvlad@transport.ro', 'abvlad', 'addjaRS439fdms');

-- Generare tabela tranzactii
DECLARE
    v_id tranzactii.id%TYPE;
    v_id_client tranzactii.id_client%TYPE;
    v_id_angajat tranzactii.id_angajat%TYPE;
    v_id_abonament tranzactii.id_abonament%TYPE;
    v_data_tranzactie tranzactii.data_tranzactie%TYPE;
    v_zi int;
    v_luna int;
BEGIN
    -- Generare 10000 de tranzactii
    FOR v_index IN 1..10000 LOOP
        -- Generare id
        SELECT NVL(MAX(id), 0) INTO v_id FROM tranzactii;
        v_id := v_id + 1;
        
        -- Generare id client
        SELECT MAX(id) INTO v_id_client FROM clienti;
        v_id_client := FLOOR(DBMS_RANDOM.VALUE(1, v_id_client+1));
        
        -- Generare id angajat
        SELECT MAX(id) INTO v_id_angajat FROM angajati;
        v_id_angajat := FLOOR(DBMS_RANDOM.VALUE(1, v_id_angajat+1));
        
        -- Generare id abonament
        SELECT MAX(id) INTO v_id_abonament FROM abonamente;
        v_id_abonament := FLOOR(DBMS_RANDOM.VALUE(1, v_id_abonament+1));
        
        -- Generare data tranzactie
        v_zi := FLOOR(DBMS_RANDOM.VALUE(1, 29));
        v_luna := FLOOR(DBMS_RANDOM.VALUE(1, 13));
        v_data_tranzactie := TO_DATE(v_zi || '-' || v_luna || '-2017', 'DD-MM-YYYY');
        
        -- Inserare in tabela
        INSERT INTO tranzactii (id, id_client, id_angajat, id_abonament, data_tranzactie)
                VALUES(v_id, v_id_client, v_id_angajat, v_id_abonament, v_data_tranzactie);
    END LOOP;
END;
/

-- Generare tabela trase
INSERT INTO trasee (id, ruta) VALUES (1, 'Copou - Fundatie - Unirii - Filarmonica - Tg. Cucu - Tudor');
INSERT INTO trasee (id, ruta) VALUES (2, 'Copou - Fundatie - Unirii - Filarmonica - Tg. Cucu - Palat - Pd. Ros - Baza 3');
INSERT INTO trasee (id, ruta) VALUES (3, 'Copou - Fundatie - Unirii - Filarmonica - Tg. Cucu - Tatarasi');
INSERT INTO trasee (id, ruta) VALUES (4, 'Nicolina - Gara - Unirii - Filarmonica - Tg. Cucu');

-- Generare tabela trasee_abonament
DECLARE
    v_id_client trasee_abonament.id_client%TYPE;
    v_id_traseu trasee_abonament.id_traseu%TYPE;
BEGIN
    FOR v_index IN 1..50 LOOP
        -- Generare id client
        SELECT MAX(id) INTO v_id_client FROM clienti;
        v_id_client := FLOOR(DBMS_RANDOM.VALUE(1, v_id_client+1));
        
        -- Generare id traseu
        SELECT MAX(id) INTO v_id_traseu FROM trasee;
        v_id_traseu := FLOOR(DBMS_RANDOM.VALUE(1, v_id_traseu+1));
        
        -- Inserare in tabela
        INSERT INTO trasee_abonament (id_client, id_traseu) VALUES (v_id_client, v_id_traseu);
    END LOOP;
END;
/