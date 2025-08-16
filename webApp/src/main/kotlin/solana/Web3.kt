@file:JsModule("@solana/web3.js")
@file:JsNonModule

package solana

import kotlin.js.Promise
import org.khronos.webgl.Uint8Array

/**
 * Externs mínimos para @solana/web3.js. Estes mapeamentos permitem que
 * o Kotlin/JS invoque APIs JavaScript diretamente, mantendo tipagem básica.
 */
external class Connection(endpoint: String, options: dynamic = definedExternally) {
    fun getBalance(pubkey: PublicKey): Promise<Number>
    fun getLatestBlockhash(): Promise<dynamic>
    fun sendRawTransaction(tx: Uint8Array): Promise<String>
}

external class PublicKey(value: String) {
    fun toBase58(): String
}

external class Transaction() {
    var feePayer: PublicKey
    var recentBlockhash: String
    fun add(vararg ix: dynamic): Transaction
    fun serialize(options: dynamic = definedExternally): Uint8Array
}

external object SystemProgram {
    fun transfer(options: dynamic): dynamic
}