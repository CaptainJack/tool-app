import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
	kotlin("jvm") version "1.3.41"
	id("nebula.release") version "11.1.0"
	id("ru.capjack.bintray") version "0.20.1"
}

group = "ru.capjack.tool"

repositories {
	jcenter()
	maven("https://dl.bintray.com/capjack/public")
	mavenLocal()
}

kotlin {
	target {
		compilations.all { kotlinOptions.jvmTarget = "1.8" }
	}
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
	implementation(kotlin("reflect"))
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.9")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
	implementation("ru.capjack.tool:tool-logging:0.14.5")
	implementation("ch.qos.logback:logback-classic:1.2.3")
	
	api("ru.capjack.tool:tool-utils:0.3.0")
	api("ru.capjack.tool:tool-depin:0.5.0")
}
