plugins {
    kotlin("multiplatform") version "2.0.0"
}

kotlin {
    // Basic configuration for common multiplatform. There is no code for now,
    // but this allows creating src/commonMain to share business logic later.
    jvm()
    js(IR) {
        nodejs()
        browser()
    }
    // Android/iOS can be added when needed.

    sourceSets {
        val commonMain by getting {}
        val commonTest by getting {}
    }
}
