@file:JsModule("@solana/web3.js")
@file:JsNonModule

package solana

/**
 * Extern para o programa de verificação Ed25519 integrado ao Solana.
 * Útil para compor instruções que checam assinaturas fora do programa
 * principal.
 */
external object Ed25519Program {
    fun createInstructionWithPublicKey(params: dynamic): dynamic
}