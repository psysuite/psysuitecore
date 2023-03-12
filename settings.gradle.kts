pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            url = uri("https://dl.bintray.com/kyonifer/maven")
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(org.gradle.api.initialization.resolve.RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

include(":nativeaudio")

//include(":nativeaudio")
//project(":nativeaudio").projectDir = File(settingsDir, "../nativeaudio/nativeaudio")


include(":psysuitepython")
project(":psysuitepython").projectDir = File(settingsDir, "../psysuitepython/psysuitepython")

include(":core")
project(":core").projectDir = File(settingsDir, "../core/core")

include(":psysuitecore")
