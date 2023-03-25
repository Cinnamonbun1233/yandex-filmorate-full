# Filmorate

### ER diagram
![filmorate ER diagram](src/docs/filmorate_er_diagram.png)


### Constaints
* Table **like** has compound PK (user_id, film.film_id)
* Table **connection** has compound PK (user_from, user_to)

### Enums
* **connection_type**: 'not_approved', 'approved'. Could be expanded
* **rating**: MPA 'G', 'PG', 'PG-13, 'R', 'NC-17'. Correlates with [MPA](https://en.wikipedia.org/wiki/Motion_Picture_Association) rating

### Requst expamples

#### top 10 fiilms
``` sql
SELECT
    *
FROM
    film
WHERE
    film IN (
        SELECT
            film_id,
            SUM(user_id) AS likes
        FROM
            like
        GROUP BY
            film_id
        ORDER BY
            likes
        LIMIT(10))
```

#### approved friends of a user
``` sql
SELECT
    *
FROM
    user
WHERE
    user_id IN (
        SELECT
            user_to
        FROM
            connection
        WHERE
            user_from = %id
        AND connection_type = 'approved')
```
