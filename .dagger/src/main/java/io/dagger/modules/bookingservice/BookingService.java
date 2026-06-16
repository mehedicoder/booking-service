package io.dagger.modules.bookingservice;

import static io.dagger.client.Dagger.dag;

import io.dagger.client.Container;
import io.dagger.client.Directory;
import io.dagger.client.File;
import io.dagger.client.Secret;
import io.dagger.client.Service;
import io.dagger.client.exception.DaggerQueryException;
import io.dagger.module.annotation.Function;
import io.dagger.module.annotation.Object;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** CI/CD pipeline for the booking service. */
@Object
public class BookingService {
    private static final String WORKDIR = "/app";
    private static final String POSTGRES_DB = "booking";
    private static final String POSTGRES_USER = "booking";
    private static final String POSTGRES_PASS = "booking";

    /** Runs the Spring Boot test suite against an isolated PostgreSQL service. */
    @Function
    public Container test(Directory source) {
        Service postgres = dag()
                .container()
                .from("postgres:16-alpine")
                .withEnvVariable("POSTGRES_DB", POSTGRES_DB)
                .withEnvVariable("POSTGRES_USER", POSTGRES_USER)
                .withEnvVariable("POSTGRES_PASSWORD", POSTGRES_PASS)
                .withExposedPort(5432)
                .asService();

        return maven(source)
                .withServiceBinding("postgres", postgres)
                .withEnvVariable("SPRING_DATASOURCE_URL", "jdbc:postgresql://postgres:5432/" + POSTGRES_DB)
                .withEnvVariable("SPRING_DATASOURCE_USERNAME", POSTGRES_USER)
                .withEnvVariable("SPRING_DATASOURCE_PASSWORD", POSTGRES_PASS)
                .withEnvVariable("SPRING_JPA_HIBERNATE_DDL_AUTO", "create-drop")
                .withExec(List.of("mvn", "test"));
    }

    /** Runs PostgreSQL-backed tests, builds the application image, and publishes it to Docker Hub. */
    @Function
    public String publish(
            Directory source,
            String imageRef,
            String dockerhubUsername,
            Secret dockerhubToken)
            throws InterruptedException, ExecutionException, DaggerQueryException {
        test(source).sync();

        File jar = maven(source)
                .withExec(List.of("mvn", "clean", "package", "-DskipTests"))
                .file("target/booking-service-1.0-SNAPSHOT.jar");

        return dag()
                .container()
                .from("eclipse-temurin:21-jre")
                .withWorkdir(WORKDIR)
                .withFile("app.jar", jar)
                .withExposedPort(8080)
                .withEntrypoint(List.of("java", "-jar", "app.jar"))
                .withRegistryAuth("docker.io", dockerhubUsername, dockerhubToken)
                .publish(imageRef);
    }

    private Container maven(Directory source) {
        return dag()
                .container()
                .from("maven:3.9.9-eclipse-temurin-21")
                .withMountedDirectory(WORKDIR, source)
                .withWorkdir(WORKDIR);
    }
}
