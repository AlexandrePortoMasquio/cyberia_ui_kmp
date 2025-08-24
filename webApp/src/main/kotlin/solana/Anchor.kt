@file:JsModule("@coral-xyz/anchor")
@file:JsNonModule

package solana

/**
 * Minimal externs for Anchor in the JS environment. With these bindings
 * it is possible to instantiate a Provider and a Program from an IDL.
 */
external object anchor {
    val web3: dynamic
    class Program(idl: dynamic, programId: String, provider: dynamic)
    class AnchorProvider(connection: dynamic, wallet: dynamic, opts: dynamic)
}

/**
 * Dynamic module require. In some Kotlin/JS contexts it is convenient
 * to use require() to load modules.
 */
external fun require(module: String): dynamic
