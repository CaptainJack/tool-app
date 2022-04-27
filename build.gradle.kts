plugins {
	kotlin("jvm") version "1.6.21"
	id("ru.capjack.publisher") version "1.0.0"
}

group = "ru.capjack.tool"

repositories {
	mavenCentral()
	mavenCapjack()
}

kotlin {
	target.compilations.all { kotlinOptions.jvmTarget = "11" }
}

dependencies {
	api("ru.capjack.tool:tool-lang:1.12.0")
	api("ru.capjack.tool:tool-logging:1.6.0")
	api("ru.capjack.tool:tool-utils:1.8.0")
	api("ru.capjack.tool:tool-depin:1.2.0")
	
	implementation(kotlin("reflect"))
	implementation("ch.qos.logback:logback-classic:1.2.10")
	
	val jackson = "2.13.2"
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jackson")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-properties:$jackson")
}
