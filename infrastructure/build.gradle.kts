import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java-conventions")
    id("application")
    id("jacoco")
    id("jacoco-report-aggregation")
    id("org.sonarqube") version Version.SONARQUBE
    id("org.springframework.boot") version Version.SPRING_BOOT
    id("io.spring.dependency-management") version Version.SPRING_DEPENDENCY_MANAGEMENT
    id("io.gatling.gradle") version Version.GATLING
    kotlin("jvm") version Version.KOTLIN
    kotlin("plugin.spring") version Version.KOTLIN
}

group = "com.lukinhasssss.catalogo.infrastructure"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks.withType<BootJar> {
    archiveBaseName.set("application")
    destinationDirectory.set(file("${rootProject.buildDir}/libs"))
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.module:jackson-module-afterburner")

    implementation("org.springdoc:springdoc-openapi-webmvc-core:${Version.OPEN_API}")
    implementation("org.springdoc:springdoc-openapi-ui:${Version.OPEN_API}")

    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow") {
        exclude(group = "io.undertow", module = "undertow-websockets-jsr")
    }

    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus:1.10.5")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("ch.qos.logback:logback-core:1.4.14")
    implementation("net.logstash.logback:logstash-logback-encoder:7.3")

    implementation("com.google.guava:guava:32.0.0-android")

    testImplementation(project(path = ":domain", configuration = "testClasses"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.graphql:spring-graphql-test")
    testImplementation("org.springframework.security:spring-security-test")

    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("io.rest-assured:kotlin-extensions:5.3.0")

    testImplementation("org.testcontainers:testcontainers:${Version.TEST_CONTAINERS}")
    testImplementation("org.testcontainers:elasticsearch:${Version.TEST_CONTAINERS}")
    testImplementation("org.testcontainers:junit-jupiter:${Version.TEST_CONTAINERS}")
    testImplementation("com.github.dasniko:testcontainers-keycloak:2.5.0")
    testImplementation("org.keycloak:keycloak-core:23.0.4")
    testImplementation("org.jboss.resteasy:resteasy-core:4.7.9.Final")
    testImplementation("org.jboss.resteasy:resteasy-multipart-provider:4.7.9.Final")
}

tasks.testCodeCoverageReport {
    reports {
        xml.required.set(true)
        xml.outputLocation.set(file("${projectDir.parentFile.path}/build/reports/jacoco/test/jacocoTestReport.xml"))

        html.required.set(true)
        html.outputLocation.set(file("${projectDir.parentFile.path}/build/reports/jacoco/test/"))
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.testCodeCoverageReport)
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}
