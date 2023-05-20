import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.report.ReportMergeTask

plugins {
    kotlin("jvm") version Version.KOTLIN
    id("org.sonarqube") version Version.SONARQUBE
    id("io.gitlab.arturbosch.detekt") version Version.DETEKT
    id("org.jlleitschuh.gradle.ktlint") version Version.KTLINT apply(false)
}

group = "com.lukinhasssss.catalogo"

repositories {
    mavenCentral()
}

// START OF DETEKT AND KTLINT MULTI-MODULE CONFIGURATION
detekt {
    toolVersion = Version.DETEKT
    config.from(files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
}

val detektReportMerge by tasks.registering(ReportMergeTask::class) {
    output.set(rootProject.buildDir.resolve("reports/detekt/detekt-report.xml"))
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    plugins.withType(DetektPlugin::class) {
        tasks.withType(Detekt::class) detekt@{
            finalizedBy(detektReportMerge)

            detektReportMerge.configure {
                input.from(this@detekt.xmlReportFile)
            }
        }
    }
}
// END OF DETEKT AND KTLINT MULTI-MODULE CONFIGURATION

// START OF SONAR MULTI-MODULE CONFIGURATION
sonar {
    properties {
        property("sonar.projectKey", "Lukinhasssss_catalogo-de-videos")
        property("sonar.projectName", "catalogo-de-videos")
        property("sonar.organization", "lukinhasssss")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${projectDir.parentFile.path}/build/reports/jacoco/test/jacocoTestReport.xml"
        )
        property("sonar.exclusions", "build/generated/**/*")
    }
}

subprojects {
    sonar {
        properties {
            property(
                "sonar.coverage.jacoco.xmlReportPaths",
                "${projectDir.parentFile.path}/build/reports/jacoco/test/jacocoTestReport.xml"
            )
        }
    }
}
