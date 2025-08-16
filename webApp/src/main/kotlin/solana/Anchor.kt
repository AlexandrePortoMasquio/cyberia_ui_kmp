@file:JsModule("@coral-xyz/anchor")
@file:JsNonModule

package solana

/**
 * Externs mínimos para Anchor no ambiente JS. Com estes bindings
 * é possível instanciar um Provider e um Program a partir de um IDL.
 */
external object anchor {
    val web3: dynamic
    class Program(idl: dynamic, programId: String, provider: dynamic)
    class AnchorProvider(connection: dynamic, wallet: dynamic, opts: dynamic)
}

/**
 * Requer módulo dinâmico. Em alguns contextos do Kotlin/JS
 * convém usar require() para carregar módulos.
 */
external fun require(module: String): dynamic