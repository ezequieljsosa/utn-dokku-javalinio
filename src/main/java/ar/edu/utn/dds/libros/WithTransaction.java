package ar.edu.utn.dds.libros;

import io.javalin.http.Context;
import javax.persistence.EntityManager;

@FunctionalInterface
public interface WithTransaction {
    void method(Context ctx, EntityManager em);
}
