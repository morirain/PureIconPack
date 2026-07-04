plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
gradle.startParameter.excludedTaskNames.addAll(listOf(":buildSrc:testClasses"))

include(":app")
include(":library")
