@file:Suppress("UnstableApiUsage")

import java.time.format.DateTimeFormatter

plugins {
    id("maven-publish")
    id("dev.architectury.loom") version "1.6-SNAPSHOT"
}

fun prop(id: String): String = property(id) as String

base.archivesName = prop("mod_group_id")
version = prop("mod_version")
group = prop("mod_maven_group")

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

loom {
    runs {
        all {
            vmArg("-XX:+AllowEnhancedClassRedefinition")
        }

        register("data") {
            data()
            programArgs("--all", "--mod", prop("mod_id"),
                    "--output", file("src/generated/resources/").absolutePath,
                    "--existing", file("src/main/resources/").absolutePath)
        }
    }
}

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
            exclude("*.cache/")
        }
    }
}

repositories {
    maven("https://maven.parchmentmc.org")
    maven("https://maven.blamejared.com/") // JEI
    maven("https://modmaven.dev") // JEI Mirror
    maven("https://maven.terraformersmc.com") // EMI
    maven("https://maven.tterrag.com/") // Create
    maven("https://maven.theillusivec4.top/") // Curios
    maven("https://maven.terraformersmc.com") // Terrarium and Ad Astra
    maven("https://maven.teamresourceful.com/repository/maven-public/") // Resourceful Lib and Config

    // Jade
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven")
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }

    // KubeJS and Rhino
    maven("https://maven.saps.dev/minecraft") {
        mavenContent {
            includeGroup("dev.latvian.mods")
        }
    }
}

dependencies {
    val mcver = prop("minecraft_version")

    minecraft("com.mojang:minecraft:${mcver}")

    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${prop("parchment_version")}@zip")
    })

    forge("net.minecraftforge:forge:${mcver}-${prop("forge_version")}")

    // Ad Astra : Required
    modImplementation("earth.terrarium.adastra:ad_astra-forge-${mcver}:${prop("ad_astra_version")}")
    modImplementation("earth.terrarium.botarium:botarium-forge-${mcver}:${prop("botarium_version")}")
    modImplementation("com.teamresourceful.resourcefullib:resourcefullib-forge-${mcver}:${prop("resourceful_lib_version")}")
    modImplementation("com.teamresourceful.resourcefulconfig:resourcefulconfig-forge-${mcver}:${prop("resourceful_config_version")}")
    forgeRuntimeLibrary("com.teamresourceful:yabn:${prop("yabn_version")}")
    forgeRuntimeLibrary("com.teamresourceful:bytecodecs:${prop("bytecodecs_version")}")

    // JEI : Optional
    modCompileOnlyApi("mezz.jei:jei-${mcver}-common-api:${prop("jei_version")}") { isTransitive = false }
    modCompileOnlyApi("mezz.jei:jei-${mcver}-forge-api:${prop("jei_version")}") { isTransitive = false }
    modRuntimeOnly("mezz.jei:jei-${mcver}-forge:${prop("jei_version")}") { isTransitive = false }

    // EMI : Optional
    modCompileOnly("dev.emi:emi-forge:${prop("emi_version")}+${mcver}:api")
    modRuntimeOnly("dev.emi:emi-forge:${prop("emi_version")}+${mcver}")

    // Jade : Optional
    modImplementation("maven.modrinth:jade:${prop("jade_version")}")

    // Curios : Optional
    modCompileOnly("top.theillusivec4.curios:curios-forge:${prop("curios_version")}+${mcver}:api")
    if (prop("enable_curios").toBoolean()) {
        modRuntimeOnly("top.theillusivec4.curios:curios-forge:${prop("curios_version")}+${mcver}")
    }

    // Create : Optional
    modCompileOnly("com.simibubi.create:create-${mcver}:${prop("create_version")}:slim") { isTransitive = false }
    if (prop("enable_create").toBoolean()) {
        modRuntimeOnly("com.simibubi.create:create-${mcver}:${prop("create_version")}:slim") {
            isTransitive = false
        }
        modRuntimeOnly("com.jozufozu.flywheel:flywheel-forge-${mcver}:${prop("flywheel_version")}")
        modRuntimeOnly("com.tterrag.registrate:Registrate:${prop("registrate_version")}")
    }

    // KubeJS : Optional
    modImplementation("dev.latvian.mods:kubejs-forge:${prop("kubejs_version")}")
    localRuntime("io.github.llamalad7:mixinextras-forge:0.3.5")
}

tasks.withType<ProcessResources>().configureEach {
    val props = mutableMapOf(
        "mod_id" to prop("mod_id"),
        "mod_license" to prop("mod_license"),
        "mod_version" to prop("mod_version"),
        "mod_name" to prop("mod_name"),
        "mod_authors" to prop("mod_authors"),
        "mod_description" to prop("mod_description"),

        "minecraft_version" to prop("minecraft_version"),
        "minecraft_version_range" to prop("minecraft_version_range"),
        "forge_version" to prop("forge_version"),
        "forge_version_range" to prop("forge_version_range"),
        "loader_version_range" to prop("loader_version_range"),

        "ad_astra_version" to prop("ad_astra_version"),
        "create_version" to prop("create_version"),
    )

    println("processResources properties:")
    println(props)

    inputs.properties(props)

    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(props)
    }
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.jar {
    manifest {
        attributes(
                "Specification-Title" to prop("mod_id"),
                "Specification-Vendor" to prop("mod_authors"),
                "Specification-Version" to "1",
                "Implementation-Title" to prop("mod_name"),
                "Implementation-Version" to prop("mod_version"),
                "Implementation-Vendor" to prop("mod_authors"),
                "Implementation-Timestamp" to DateTimeFormatter.ISO_DATE_TIME
        )
    }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }

    repositories {
    }
}

//tasks.register("idePostSync")
