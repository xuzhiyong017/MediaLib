dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        jcenter()
        maven { setUrl("https://jitpack.io") }
        google()
    }
}
rootProject.name = "MediaLib"
include(":app")
include(":media")
