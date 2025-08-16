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

// Dependências para a interface Web.
dependencies {
    // Runtime da biblioteca padrão Kotlin para JS
    implementation(kotlin("stdlib-js"))
    // HTML DSL para construir elementos de forma declarativa (opcional)
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.8.1")
    // Corrotinas para JS
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutines.version"]}")
    // Bibliotecas JavaScript importadas via NPM para Solana e Anchor
    implementation(npm("@solana/web3.js", "^1.95.3"))
    implementation(npm("@coral-xyz/anchor", "^0.29.0"))
}

// Abre automaticamente o navegador ao rodar em modo de desenvolvimento
tasks.named<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>("browserDevelopmentRun") {
    devServer = devServer?.copy(open = true)
}