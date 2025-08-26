package js

import kotlin.js.Promise
import solana.PublicKey
import solana.Transaction

/**
 * Interfaces for interacting with the Phantom wallet extension.
 * The window.solana object exposes these members when Phantom is installed.
 */
external interface PhantomConnectResult {
    val publicKey: PublicKey
}

external interface PhantomProvider {
    val isPhantom: Boolean?
    val publicKey: PublicKey?
    fun connect(): Promise<PhantomConnectResult>
    fun disconnect(): Promise<Unit>
    fun signTransaction(tx: Transaction): Promise<Transaction>
    fun signAndSendTransaction(tx: Transaction): Promise<dynamic>
}

// Global window object (to access window.solana)
external val window: dynamic
