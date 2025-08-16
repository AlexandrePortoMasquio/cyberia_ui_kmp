package js

import kotlin.js.Promise
import solana.PublicKey
import solana.Transaction

/**
 * Interfaces para interação com a extensão Phantom (wallet). O objeto
 * window.solana expõe estes membros quando a Phantom está instalada.
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

// Objeto global window (para acessar window.solana)
external val window: dynamic