import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.6.21"
plugins {
	val kotlinVersion = "1.6.21"
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.serialization") version kotlinVersion

	id("net.mamoe.mirai-console") version "2.11.0"
}

group = "my.ktbot"
version = "1.0.0"
description = "我的 QQBot"

repositories {
	mavenLocal()
	maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
	mavenCentral()
}

dependencies {
	implementation("org.xerial:sqlite-jdbc:3.36.0.3")
	implementation("org.ktorm:ktorm-core:3.4.1")
	implementation("org.ktorm:ktorm-support-sqlite:3.4.1")
	implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
	implementation("io.ktor:ktor-client-serialization-jvm:1.6.8")
	compileOnly("org.jetbrains:annotations:23.0.0")
	// implementation("ch.qos.logback:logback-classic:1.2.11")
	// implementation("org.fusesource.jansi:jansi:2.4.0")
	// implementation("org.apache.logging.log4j:log4j-api:2.17.1")
	// implementation("org.apache.logging.log4j:log4j-core:2.17.1")
	// api("net.mamoe:mirai-logging-log4j2:2.9.2")
	// implementation("org.reflections:reflections:0.10.2")
}

mirai {
	noCoreApi = false
	noTestCore = false
	noConsole = false
	dontConfigureKotlinJvmDefault = false
	publishingEnabled = false
	jvmTarget = JavaVersion.VERSION_17
	configureShadow {
		dependencyFilter.include {
			println("include: ${it.name}")
			it.moduleGroup == "io.ktor"
		}
	}
}

tasks.withType(AbstractCompile::class.java) {
	sourceCompatibility = JavaVersion.VERSION_17.toString()
	targetCompatibility = JavaVersion.VERSION_17.toString()
}

tasks.withType(KotlinCompile::class.java) {
	kotlinOptions {
		jvmTarget = JavaVersion.VERSION_17.toString()
		freeCompilerArgs = listOf(
			"-Xjsr305=strict",
			"-opt-in=kotlin.RequiresOptIn",
			// "-Xcontext-receivers",
		)
	}
}

tasks.create("build2Jar") {
	group = "mirai"
	dependsOn += "buildPlugin"
	doLast {
		val pluginPath = "${rootDir}/plugins/"
		File(pluginPath).listFiles()?.forEach {
			if (it.isFile) {
				println("Delete File: ${it.name}")
				if (!delete(it)) {
					println("Cannot Delete File:${it.name}")
				}
			}
		}
		copy {
			from("${buildDir}/mirai/")
			into(pluginPath)
			eachFile { println("Copy File: ${name}") }
		}
	}
}
