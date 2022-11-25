package ar.edu.utn.dds.libros;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


public class AppLibros {

    private Javalin app;

    public Javalin javalinApp() {
        return app;
    }


    public static EntityManagerFactory entityManagerFactory;

    public void init() throws URISyntaxException {
        this.app = Javalin.create();
        this.startEntityManagerFactory();


        app.get("/home", (Context ctx) -> ctx.result("hola!"));

        app.get("/libros", RouteWithTransaction(LibrosController::list));

        app.get("/libros/{id}", RouteWithTransaction(LibrosController::get));

        app.delete("/libros/{id}", RouteWithTransaction(LibrosController::delete));

        app.post("/libros/", RouteWithTransaction(LibrosController::create));
    }

    public void startEntityManagerFactory() throws URISyntaxException {
        //https://stackoverflow.com/questions/8836834/read-environment-variables-in-persistence-xml-file
        Map<String, String> env = System.getenv();
        Map<String, Object> configOverrides = new HashMap<String, Object>();

        String[] keys = new String[]{
                //, "javax.persistence.jdbc.url",
                //"javax.persistence.jdbc.user", "javax.persistence.jdbc.password",
                "DATABASE_URL",
                "hibernate.show_sql",
                "ddlauto",
                "javax.persistence.jdbc.driver"
                //"javax.persistence.schema-generation.database.action"
        };

        for (String key : keys) {
            if (env.containsKey(key)) {

                if (key.equals("DATABASE_URL")) {
                    // https://devcenter.heroku.com/articles/connecting-heroku-postgres#connecting-in-java
                    String value = env.get(key);

                    URI dbUri = new URI(value);

                    String username = dbUri.getUserInfo().split(":")[0];
                    String password = dbUri.getUserInfo().split(":")[1];
                    //javax.persistence.jdbc.url=jdbc:postgresql://localhost/dblibros
                    value = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();// + "?sslmode=require";
                    configOverrides.put("javax.persistence.jdbc.url", value);
                    configOverrides.put("javax.persistence.jdbc.user", username);
                    configOverrides.put("javax.persistence.jdbc.password", password);
                    //  configOverrides.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

                }
                if (key.equals("ddlauto")) {
                    String value = env.get(key);
                    configOverrides.put("javax.persistence.schema-generation.database.action", value);
                }


            }
        }

        entityManagerFactory =
                Persistence.createEntityManagerFactory("db", configOverrides);

    }


    public void start() {
        app.start(getHerokuAssignedPort());
    }

    private static int getHerokuAssignedPort() {
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
            return Integer.parseInt(herokuPort);
        }
        return 7000;
    }

    public static void main(String[] args) throws Exception {
        AppLibros app = new AppLibros();
        app.init();
        app.start();
    }

    private static Handler RouteWithTransaction(WithTransaction fn) {
        Handler r = (ctx) -> {
            EntityManager em = entityManagerFactory.createEntityManager();
            em.getTransaction().begin();
            try {
                fn.method(ctx, em);
                em.getTransaction().commit();

            } catch (Exception ex) {
                em.getTransaction().rollback();
                throw ex;
            } finally {
                em.close();
            }
        };
        return r;
    }

}
