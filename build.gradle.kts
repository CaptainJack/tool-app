plugins {
	kotlin("jvm") version "1.3.71"
	id("nebula.release") version "14.1.1"
	id("ru.capjack.bintray") version "1.0.0"
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
	implementation("ch.qos.logback:logback-classic:1.2.3")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.3")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.10.3")
	
	api("ru.capjack.tool:tool-utils:0.10.1")
	api("ru.capjack.tool:tool-logging:1.1.0")
	api("ru.capjack.tool:tool-depin:0.6.0")
}
