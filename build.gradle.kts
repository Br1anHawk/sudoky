import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "me.dmitry"
version = "1.0"

repositories {
    mavenCentral()
}


dependencies {
    implementation("org.openpnp:opencv:4.5.1-2")
    implementation("net.sourceforge.tess4j:tess4j:5.1.1")
    implementation("technology.tabula:tabula:1.0.5")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0")
    //implementation("com.j2html:j2html:1.5.0")
    //testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
