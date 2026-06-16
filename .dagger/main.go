// CI/CD pipeline for the booking service.

package main

import (
	"context"
	"dagger/booking-service/internal/dagger"
)

type BookingService struct{}

const (
	workdir      = "/app"
	postgresDB  = "booking"
	postgresUser = "booking"
	postgresPass = "booking"
)

// Test runs the Spring Boot test suite against an isolated PostgreSQL service.
func (m *BookingService) Test(source *dagger.Directory) *dagger.Container {
	postgres := dag.Container().
		From("postgres:16-alpine").
		WithEnvVariable("POSTGRES_DB", postgresDB).
		WithEnvVariable("POSTGRES_USER", postgresUser).
		WithEnvVariable("POSTGRES_PASSWORD", postgresPass).
		WithExposedPort(5432).
		AsService()

	return m.maven(source).
		WithServiceBinding("postgres", postgres).
		WithEnvVariable("SPRING_DATASOURCE_URL", "jdbc:postgresql://postgres:5432/"+postgresDB).
		WithEnvVariable("SPRING_DATASOURCE_USERNAME", postgresUser).
		WithEnvVariable("SPRING_DATASOURCE_PASSWORD", postgresPass).
		WithEnvVariable("SPRING_JPA_HIBERNATE_DDL_AUTO", "create-drop").
		WithExec([]string{"mvn", "test"})
}

// Publish runs PostgreSQL-backed tests, builds the application image, and publishes it to Docker Hub.
func (m *BookingService) Publish(
	ctx context.Context,
	source *dagger.Directory,
	imageRef string,
	dockerhubUsername string,
	dockerhubToken *dagger.Secret,
) (string, error) {
	_, err := m.Test(source).Sync(ctx)
	if err != nil {
		return "", err
	}

	jar := m.maven(source).
		WithExec([]string{"mvn", "clean", "package", "-DskipTests"}).
		File("target/booking-service-1.0-SNAPSHOT.jar")

	return dag.Container().
		From("eclipse-temurin:21-jre").
		WithWorkdir(workdir).
		WithFile("app.jar", jar).
		WithExposedPort(8080).
		WithEntrypoint([]string{"java", "-jar", "app.jar"}).
		WithRegistryAuth("docker.io", dockerhubUsername, dockerhubToken).
		Publish(ctx, imageRef)
}

func (m *BookingService) maven(source *dagger.Directory) *dagger.Container {
	return dag.Container().
		From("maven:3.9.9-eclipse-temurin-21").
		WithMountedDirectory(workdir, source).
		WithWorkdir(workdir)
}
