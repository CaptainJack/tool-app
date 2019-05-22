import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
	kotlin("jvm") version "1.3.21"
	id("nebula.release") version "10.1.1"
	id("ru.capjack.bintray") version "0.17.0"
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
	implementation("ru.capjack.tool:tool-reflect-jvm:0.11.0")
	
	api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.+")
	api("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")
	
	api("ru.capjack.tool:tool-logging-jvm:0.13.0")
	api("ru.capjack.tool:tool-depin-jvm:0.4.1")
	api("ru.capjack.tool:tool-lang-jvm:0.3.0")
	api("ru.capjack.tool:tool-utils-jvm:0.2.0")
}

tasks.withType<KotlinJvmCompile> {
	kotlinOptions.jvmTarget = "1.8"
}