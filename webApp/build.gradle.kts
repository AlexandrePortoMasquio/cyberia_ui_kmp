plugins {
    kotlin("multiplatform") version "2.0.0"
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport { enabled.set(true) }
            }
        }
    }

    sourceSets {
        val jsMain by getting {
            kotlin.srcDirs("src/main/kotlin")
            resources.srcDirs("src/main/resources")
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.11.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${providers.gradleProperty("coroutines.version").get()}")
                implementation(npm("@solana/web3.js", "^1.95.3"))
                implementation(npm("@coral-xyz/anchor", "^0.29.0"))
                implementation(npm("marked", "^12.0.1"))
            }
        }
    }
}
