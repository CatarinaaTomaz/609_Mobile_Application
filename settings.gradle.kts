pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Olha para esta linha com atenção: NÃO tem "path ="
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "609_ProjetoFinal"
// Olha para esta linha com atenção: NÃO tem "...projectPaths ="
include(":app")