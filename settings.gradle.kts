pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        //noinspection JcenterRepositoryObsolete
        jcenter()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        //noinspection JcenterRepositoryObsolete
        jcenter()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}

rootProject.name = "Cat_Gallery"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
