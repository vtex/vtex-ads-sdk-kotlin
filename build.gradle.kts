plugins {
    kotlin("jvm") version "1.9.22"
    id("maven-publish")
    id("java-library")
}

group = "com.vtex.ads"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Kotlin Standard Library
    implementation(kotlin("stdlib"))

    // Coroutines for async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // HTTP Client (OkHttp)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // JSON Serialization
    implementation("com.squareup.moshi:moshi:1.15.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("VTEX Ads SDK for Kotlin")
                description.set("A Kotlin SDK for VTEX Ads API")
                url.set("https://github.com/vtex/vtex-ads-sdk-kotlin")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("vtex")
                        name.set("VTEX")
                        email.set("dev@vtex.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/vtex/vtex-ads-sdk-kotlin.git")
                    developerConnection.set("scm:git:ssh://github.com/vtex/vtex-ads-sdk-kotlin.git")
                    url.set("https://github.com/vtex/vtex-ads-sdk-kotlin")
                }
            }
        }
    }
}
