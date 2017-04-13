CREATE OR REPLACE PACKAGE project_functions IS
  TYPE spent_sums_type IS TABLE OF NUMBER INDEX BY VARCHAR2(32) ;
  FUNCTION best_client(v_id_ghiseu IN VARCHAR2) RETURN VARCHAR;
END ;
/
CREATE OR REPLACE PACKAGE BODY project_functions AS
  FUNCTION best_client (v_id_ghiseu IN VARCHAR2)
    RETURN VARCHAR AS
    v_best_client VARCHAR(64);
    v_spent_sums spent_sums_type;
    CURSOR clients_cursor IS SELECT id FROM CLIENTI;
    v_id_client CLIENTI.ID%TYPE ;
    v_spent_sum NUMBER;
    v_maxim_spent_sum NUMBER;
    v_maxim_spent_sum_index NUMBER;
    v_index varchar2(32);
  BEGIN
    v_maxim_spent_sum := 0;
    v_best_client := 'test';
    OPEN clients_cursor;
    /*
    pun toate sumele cheltuite de clientii unui anumit ghiseu
    intr-un associative array
     */
    LOOP
      FETCH clients_cursor INTO v_id_client;
      EXIT WHEN clients_cursor%NOTFOUND;
      /*
      pun in v_spent_sum suma cheltuielilor clientilor
      cu id_ul v_id_client in ulimele 12 luni
      SELECT sum(a.pret) FROM
        TRANZACTII t JOIN ABONAMENTE a ON t.ID_ABONAMENT = a.ID
        JOIN ANGAJATI ang ON ang.ID = t.ID_ANGAJAT
      WHERE t.ID_CLIENT = 1049 AND ang.ID_GHISEU = 1
      AND add_months(t.DATA_TRANZACTIE, 12) >= sysdate;
      */
      SELECT sum(a.PRET) INTO v_spent_sum FROM
        TRANZACTII t JOIN ANGAJATI ang ON ang.ID = t.ID_ANGAJAT
        JOIN ABONAMENTE a ON t.ID_ABONAMENT = a.ID
      WHERE ang.ID_GHISEU = v_id_ghiseu AND t.ID_CLIENT = v_id_client
      AND add_months(t.DATA_TRANZACTIE, 12) >= sysdate;
      v_spent_sums(v_id_client) := v_spent_sum;
    END LOOP;
    /*
    scot maximul din associative array-ul construit
     */
    v_index := v_spent_sums.FIRST;
    WHILE (v_index IS NOT NULL )
    LOOP

      if(v_spent_sums(v_index) > v_maxim_spent_sum) THEN
        v_maxim_spent_sum := v_spent_sums(v_index);
        v_maxim_spent_sum_index := to_char(v_index);
      END IF;
      v_index := v_spent_sums.NEXT(v_index);
    END LOOP;
    /*
    pentru id-ul aflat mai sus scot numele si prenumele clientului
     */
    SELECT CLIENTI.NUME||' '||CLIENTI.PRENUME INTO v_best_client FROM CLIENTI
      WHERE ID = v_maxim_spent_sum_index;
    RETURN v_best_client;
    EXCEPTION
      WHEN NO_DATA_FOUND
      THEN
      RETURN 'Nobody';
    RAISE ;
  END;
END project_functions;
/

-- ================================= CLIENT PACK =================================
CREATE OR REPLACE PACKAGE client_pack AS
    PROCEDURE update_client(p_firstname IN VARCHAR2, p_lastname IN VARCHAR2, p_cnp IN VARCHAR2, p_email IN VARCHAR2);
    PROCEDURE remove_client(p_cnp IN VARCHAR2);
END client_pack;
/

CREATE OR REPLACE PACKAGE BODY client_pack AS
    PROCEDURE update_client(p_firstname IN VARCHAR2, p_lastname IN VARCHAR2, p_cnp IN VARCHAR2, p_email IN VARCHAR2) IS
    BEGIN
        UPDATE clienti SET nume = p_firstname, prenume = p_lastname, email = p_email WHERE cnp = p_cnp;
    END;
    
    PROCEDURE remove_client(p_cnp IN VARCHAR2) IS
    BEGIN
        DELETE FROM clienti WHERE cnp = p_cnp;
    END;
END client_pack;
/

-- ================================= TRANSACTION PACK =================================
CREATE OR REPLACE PACKAGE transaction_pack AS
    PROCEDURE make_transaction(p_cnp IN VARCHAR2, p_id_angajat IN NUMBER, p_id_abonament IN NUMBER);
END transaction_pack;
/

CREATE OR REPLACE PACKAGE BODY transaction_pack AS
    PROCEDURE make_transaction(p_cnp IN VARCHAR2, p_id_angajat IN NUMBER, p_id_abonament IN NUMBER) IS
        v_id_client int;
        v_id_tranzactie int;
    BEGIN
        -- Obtinere id client
        SELECT id INTO v_id_client FROM clienti WHERE cnp = p_cnp;
        
        -- Obtinere id tranzactie noua
        SELECT COUNT(*)+1 INTO v_id_tranzactie FROM tranzactii;
        
        -- Creeare tranzactie
        INSERT INTO tranzactii (id, id_client, id_angajat, id_abonament, data_tranzactie)
                VALUES (v_id_tranzactie, v_id_client, p_id_angajat, p_id_abonament, SYSDATE);
                
        -- Modificare abonament client
        UPDATE clienti SET abonament_activ = p_id_abonament, inceput_abonament = SYSDATE,
                sfarsit_abonament = SYSDATE+30 WHERE cnp = p_cnp;
    END;
END transaction_pack;
