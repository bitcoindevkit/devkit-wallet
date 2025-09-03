rootProject.name = "Devkit Wallet — 0.32 Variant"
include("app")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()

        // snapshot repository
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

        // Local Maven (~/.m2/repository/)
        // mavenLocal()
    }
}
