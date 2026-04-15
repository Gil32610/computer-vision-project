rootProject.name = "ARMeasurment"
include(":app")

dependencyResolutionManagement{
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositores{
        google()
        mavenCentral()
        maven {url = uri("https://jitpack.io")}
    }
}