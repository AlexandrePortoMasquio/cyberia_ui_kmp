plugins {
    kotlin("multiplatform") version rootProject.extra["kotlin.version"] as String
}

kotlin {
    // Configurações básicas para multiplataforma comum. Por ora não há código,
    // mas esta configuração permite criar src/commonMain para compartilhar
    // lógica de negócios futuramente.
    jvm()
    js(IR) {
        nodejs()
        browser()
    }
    // Android/iOS podem ser adicionados quando necessário.
}

sourceSets {
    val commonMain by getting {}
    val commonTest by getting {}
}