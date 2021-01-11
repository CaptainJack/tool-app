plugins {
	kotlin("jvm") version "1.4.21"
	id("nebula.release") version "15.3.1"
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
		compilations.all { kotlinOptions.jvmTarget = "11" }
	}
}

dependencies {
	implementation(kotlin("reflect"))
	implementation("ch.qos.logback:logback-classic:1.2.3")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.1")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.1")
	
	api("ru.capjack.tool:tool-utils:1.2.0")
	api("ru.capjack.tool:tool-logging:1.2.2")
	api("ru.capjack.tool:tool-depin:0.9.0")
}
