pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()

        mavenCentral()  // if you also use mavenCentral
    }
}

rootProject.name = "NoteSync"
include(":app")
 