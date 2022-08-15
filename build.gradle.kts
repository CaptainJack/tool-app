plugins {
	kotlin("jvm") version "1.7.10"
	id("ru.capjack.publisher") version "1.0.0"
}

group = "ru.capjack.tool"

repositories {
	mavenCentral()
	mavenCapjack()
}

kotlin {
	target.compilations.all { kotlinOptions.jvmTarget = "17" }
}

dependencies {
	api("ru.capjack.tool:tool-lang:1.13.+")
	api("ru.capjack.tool:tool-logging:1.7.+")
	api("ru.capjack.tool:tool-utils:1.9.+")
	api("ru.capjack.tool:tool-depin:1.3.+")
	
	implementation(kotlin("reflect"))
	implementation("ch.qos.logback:logback-classic:1.2.+")
	
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.+")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.+")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-properties:2.13.+")
}
