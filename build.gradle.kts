plugins {
	kotlin("jvm") version "1.5.10"
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
	api("ru.capjack.tool:tool-lang:1.11.1")
	api("ru.capjack.tool:tool-logging:1.5.0")
	api("ru.capjack.tool:tool-utils:1.6.1")
	api("ru.capjack.tool:tool-depin:1.0.0")
	
	implementation(kotlin("reflect"))
	implementation("ch.qos.logback:logback-classic:1.2.3")
	
	val jackson = "2.12.3"
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jackson")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-properties:$jackson")
}
