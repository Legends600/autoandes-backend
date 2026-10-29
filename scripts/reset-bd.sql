-- Deja el esquema AUTOANDES vacio para que Flyway vuelva a crear las tablas y cargar los datos semilla
-- al siguiente arranque del backend. Ejecutar conectado como el usuario autoandes:
--   sqlplus autoandes/<clave>@localhost:1522/FREEPDB1 @scripts/reset-bd.sql
BEGIN
  FOR t IN (SELECT table_name FROM user_tables) LOOP
    EXECUTE IMMEDIATE 'DROP TABLE "' || t.table_name || '" CASCADE CONSTRAINTS PURGE';
  END LOOP;
  FOR s IN (SELECT sequence_name FROM user_sequences) LOOP
    EXECUTE IMMEDIATE 'DROP SEQUENCE "' || s.sequence_name || '"';
  END LOOP;
END;
/
EXIT;
