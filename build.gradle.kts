import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
	kotlin("jvm") version "1.3.21"
	id("nebula.release") version "9.2.0"
	id("ru.capjack.capjack-bintray") version "0.16.1"
}

group = "ru.capjack.tool"

repositories {
	jcenter()
	maven("https://dl.bintray.com/capjack/public")
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
	implementation(kotlin("reflect"))
	
	implementation("ch.qos.logback:logback-classic:1.2.+")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.+")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")
	
	api("ru.capjack.tool:kt-logging-jvm:0.10.0")
	api("ru.capjack.tool:kt-reflect-jvm:0.10.0")
	api("ru.capjack.tool:kt-inject-jvm:0.3.0")
	api("ru.capjack.tool:kt-utils-jvm:0.1.0")
}

tasks.withType<KotlinJvmCompile> {
	kotlinOptions.jvmTarget = "1.8"
}