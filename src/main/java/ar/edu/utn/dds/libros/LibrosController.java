package ar.edu.utn.dds.libros;

import io.javalin.http.Context;

import javax.persistence.EntityManager;


public class LibrosController {

    private RepoLibros repo;


    public LibrosController(RepoLibros repo) {
        this.repo = repo;

    }

    public static void list(Context ctx, EntityManager em) {
        RepoLibros repo = new RepoLibros(em);
        String precio_max = ctx.queryParam("precio_max");
        if (precio_max != null) {
            Long precioMax = Long.parseLong(precio_max);
            if (precioMax != null) {
                ctx.json(repo.findByMaxPrecio(precioMax));
            }
        } else {
            ctx.json(repo.findAll());
        }

    }

    public static void get(Context ctx, EntityManager em) {
        RepoLibros repo = new RepoLibros(em);
        Long id = Long.parseLong(ctx.pathParam("id"));
        ctx.json(repo.findById(id));
    }

    public static void delete(Context ctx, EntityManager em) {
        RepoLibros repo = new RepoLibros(em);
        Long id = Long.parseLong(ctx.pathParam("id"));
        repo.delete(repo.findById(id));
        ctx.json("deleted");
    }


    public static void create(Context ctx, EntityManager em) {
        RepoLibros repo = new RepoLibros(em);
        Libro libro = ctx.bodyAsClass(Libro.class);
        repo.save(libro);
        ctx.status(201);
    }



}
