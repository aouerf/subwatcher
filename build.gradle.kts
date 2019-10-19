buildscript {
    val versions = mapOf(
        "coroutines" to "1.3.2",

        "lifecycle" to "2.2.0-beta01",
        "savedstate" to "1.0.0-beta01",

        "dagger" to "2.24",
        "assistedinject" to "0.5.0",

        "okhttp" to "4.2.2",
        "retrofit" to "2.6.2",
        "moshi" to "1.8.0",

        "sqldelight" to "1.2.0",

        "timber" to "5.0.0-SNAPSHOT"
    )
    extra.set("versions", versions)

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.6.0-beta01")
        classpath(kotlin("gradle-plugin", "1.3.50"))
        classpath("com.squareup.sqldelight:gradle-plugin:${versions["sqldelight"]}")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
