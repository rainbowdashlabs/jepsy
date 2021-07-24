plugins {
    java
}

group = "de.chojo"
version = "1.0"
val log4jVersion = "2.14.0"

repositories {
    mavenCentral()
    maven("https://eldonexus.de/repository/maven-public")
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation("de.chojo", "cjda-util", "1.5.5-DEV")
    implementation("net.dv8tion:JDA:4.3.0_298")
    implementation("org.jsoup:jsoup:1.14.1")
    implementation("io.github.furstenheim:copy_down:1.0")
    implementation("de.chojo:sql-util:1.0.2")
    implementation("org.postgresql", "postgresql", "42.2.22")

    // Logging
    implementation("org.slf4j", "slf4j-api", "1.7.30")
    implementation("org.apache.logging.log4j", "log4j-core", log4jVersion)
    implementation("org.apache.logging.log4j", "log4j-slf4j-impl", log4jVersion)

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java{
    withJavadocJar()
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_15
}

tasks{
    test{
        useJUnitPlatform()
    }

    compileJava{
        options.encoding = "UTF-8"
    }

    compileTestJava{
        options.encoding = "UTF-8"
    }
}
