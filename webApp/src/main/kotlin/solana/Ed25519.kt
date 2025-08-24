@file:JsModule("@solana/web3.js")
@file:JsNonModule

package solana

/**
 * Extern for the built-in Solana Ed25519 verification program.
 * Useful to compose instructions that check signatures outside the
 * main program.
 */
external object Ed25519Program {
    fun createInstructionWithPublicKey(params: dynamic): dynamic
}
