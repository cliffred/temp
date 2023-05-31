plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("pl.allegro.tech.build.axion-release") version "1.15.3"
}

buildscript {
    dependencies{
        classpath("net.java.dev.jna:jna:5.7.0")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.arrow-kt:arrow-core:1.2.0-RC")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest("1.8.20")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
