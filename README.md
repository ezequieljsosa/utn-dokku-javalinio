# utn-dokku-javalinio
Ejemplo CRUD API JAVALIN para desplegar en Dokku

Tiene solo la dependencia a Postgres (la que tiene por defecto el PaaS), si van a usar otra db para pruebas locales, tienen
que agregar la misma al pom.xml.

En ar.edu.utn.dds.libros.AppLibros se leen las variables de entorno necesarias:
- DATABASE_URL: en el PaaS se carga sola, a nivel local hay que poner el connection string, por ejemplo jdbc:postgresql://localhost/dblibros
- ddlauto: valores de javax.persistence.schema-generation.database.action.


---
Ejemplo para levantar postgres con Docker (no hace falta, pueden descargar el instalador directamente)
```
docker run -d --name ddspostgres -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword\
    -e PGDATA=/var/lib/postgresql/data/pgdata -v ${PWD}/posgresdb:/var/lib/postgresql/data postgres:14

```
