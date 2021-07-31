dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { setUrl("https://jitpack.io") }
        google()
        mavenCentral()
    }
}
rootProject.name = "MediaLib"
include(":app")
include(":media")
