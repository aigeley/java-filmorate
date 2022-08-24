INSERT INTO genres (genre_id, genre_name)
SELECT *
FROM (SELECT NEXT VALUE FOR genres_seq, 'Комедия'
      FROM dual
      UNION
      SELECT NEXT VALUE FOR genres_seq, 'Драма'
      FROM dual
      UNION
      SELECT NEXT VALUE FOR genres_seq, 'Мультфильм'
      FROM dual
      UNION
      SELECT NEXT VALUE FOR genres_seq, 'Триллер'
      FROM dual
      UNION
      SELECT NEXT VALUE FOR genres_seq, 'Документальный'
      FROM dual
      UNION
      SELECT NEXT VALUE FOR genres_seq, 'Боевик'
      FROM dual)
WHERE NOT EXISTS(SELECT * FROM genres);
INSERT INTO mpa (mpa_id, mpa_name)
SELECT *
FROM (SELECT NEXT VALUE FOR mpa_seq, 'G'
      FROM dual
      UNION
      SELECT NEXT VALUE FOR mpa_seq, 'PG'
      FROM dual
      UNION
      SELECT NEXT VALUE FOR mpa_seq, 'PG-13'
      FROM dual
      UNION
      SELECT NEXT VALUE FOR mpa_seq, 'R'
      FROM dual
      UNION
      SELECT NEXT VALUE FOR mpa_seq, 'NC-17'
      FROM dual)
WHERE NOT EXISTS(SELECT * FROM mpa);