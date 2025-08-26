@file:JsModule("@solana/web3.js")
@file:JsNonModule

package solana

import kotlin.js.Promise
import org.khronos.webgl.Uint8Array

/**
 * Minimal externs for @solana/web3.js. These mappings allow Kotlin/JS
 * to call JavaScript APIs directly with basic typing.
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
