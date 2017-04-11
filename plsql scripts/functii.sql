set serveroutput on;

CREATE OR REPLACE PACKAGE bus_app_pack AS
    TYPE Statistica IS RECORD(nume abonamente.tip%TYPE, procent NUMBER);
    TYPE RaspunsStatisticaAbonamente IS TABLE OF Statistica INDEX BY PLS_INTEGER;
    PROCEDURE statistica_abonamente(p_statistica_abonamente OUT RaspunsStatisticaAbonamente);
END bus_app_pack;
/

CREATE OR REPLACE PACKAGE BODY bus_app_pack AS
    PROCEDURE statistica_abonamente(p_statistica_abonamente OUT RaspunsStatisticaAbonamente) IS
        CURSOR lista_clienti IS SELECT abonament_activ, inceput_abonament, sfarsit_abonament FROM clienti;
        v_tip_abonament clienti.abonament_activ%TYPE;
        v_data_inceput clienti.inceput_abonament%TYPE;
        v_data_sfarsit clienti.sfarsit_abonament%TYPE;
        v_nr_abonamente_active int;
        v_nume_abonament abonamente.tip%TYPE;
    BEGIN
        OPEN lista_clienti;
        
        v_nr_abonamente_active := 0;
        
        LOOP
            FETCH lista_clienti INTO v_tip_abonament, v_data_inceput, v_data_sfarsit;
            EXIT WHEN lista_clienti%NOTFOUND;
            
            IF v_tip_abonament != -1 THEN
                IF p_statistica_abonamente.exists(v_tip_abonament) THEN
                    p_statistica_abonamente(v_tip_abonament).procent := p_statistica_abonamente(v_tip_abonament).procent + 1;
                ELSE
                    p_statistica_abonamente(v_tip_abonament).procent := 1;
                END IF;
            END IF;
            v_nr_abonamente_active := v_nr_abonamente_active + 1;
        END LOOP;
        
        CLOSE lista_clienti;
        
        FOR v_i IN p_statistica_abonamente.first .. p_statistica_abonamente.last LOOP
            SELECT tip INTO v_nume_abonament FROM abonamente WHERE id = v_i;
            p_statistica_abonamente(v_i).nume := v_nume_abonament;
            p_statistica_abonamente(v_i).procent := p_statistica_abonamente(v_i).procent / v_nr_abonamente_active * 100;
        END LOOP;
    END;
END bus_app_pack;
/

-- TEST AREA --
DECLARE
    stat bus_app_pack.RaspunsStatisticaAbonamente;
BEGIN
    bus_app_pack.statistica_abonamente(stat);
    FOR v_i IN stat.first..stat.last LOOP
        DBMS_OUTPUT.PUT_LINE('Abonament: ' || stat(v_i).nume || ' Procent: ' || TO_CHAR(stat(v_i).procent) || '%');
    END LOOP;
END;