import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform") version rootProject.extra["kotlin.version"] as String
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
}

// Dependencies for the Web interface.
dependencies {
    // Kotlin standard library for JS runtime
    implementation(kotlin("stdlib-js"))
    // HTML DSL to build elements declaratively (optional)
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.8.1")
    // Coroutines for JS
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutines.version"]}")
    // JavaScript libraries imported via NPM for Solana and Anchor
    implementation(npm("@solana/web3.js", "^1.95.3"))
    implementation(npm("@coral-xyz/anchor", "^0.29.0"))
}

// Automatically open the browser when running in development mode
tasks.named<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>("browserDevelopmentRun") {
    // In CI/web testing we don't want to auto-open the browser.
    val openBrowser = (System.getenv("CI") ?: "false").lowercase() != "true"
    devServer = devServer?.copy(open = openBrowser)
}
