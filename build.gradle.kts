import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
	kotlin("jvm") version "1.3.11"
	id("nebula.release") version "9.2.0"
	id("ru.capjack.capjack-bintray") version "0.14.1"
}

group = "ru.capjack.ktjvm.app"

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
	
	api("ru.capjack.kt.logging:kt-logging-jvm:0.8.+")
	api("ru.capjack.kt.reflect:kt-reflect-jvm:0.8.+")
	api("ru.capjack.kt.inject:kt-inject-jvm:0.1.+")
	api("ru.capjack.kt.utils:kt-utils-jvm:0.1.+")
}

tasks.withType<KotlinJvmCompile> {
	kotlinOptions.jvmTarget = "1.8"
}