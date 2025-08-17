import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import solana.Connection
import solana.PublicKey
import js.PhantomProvider
import js.window as jsWindow
import org.w3c.dom.HTMLInputElement

/**
 * Entrypoint do aplicativo Kotlin/JS. Este código inicializa
 * a conexão com a carteira Phantom, consulta saldo e registra
 * handlers para botões de escrow. As funções de escrow ainda
 * estão stubs, pois dependem do IDL real do contrato.
 */
private val defaultEndpoint: String = js("(typeof process !== 'undefined' && process.env.SOLANA_RPC) || ''") as? String?
    ?: "https://api.devnet.solana.com"

private fun el(id: String) = document.getElementById(id)!!

fun main() {
    // Exibir endpoint em algum lugar, se desejar.
    val connectBtn = el("connectBtn") as org.w3c.dom.HTMLButtonElement
    val balanceBtn = el("balanceBtn") as org.w3c.dom.HTMLButtonElement
    val openEscrowBtn = el("openEscrowBtn") as org.w3c.dom.HTMLButtonElement
    val confirmBtn = el("confirmBtn") as org.w3c.dom.HTMLButtonElement
    val refundBtn = el("refundBtn") as org.w3c.dom.HTMLButtonElement

    val provider = jsWindow.asDynamic().solana as? PhantomProvider
    if (provider == null || provider.isPhantom != true) {
        connectBtn.disabled = true
        balanceBtn.disabled = true
        openEscrowBtn.disabled = true
        confirmBtn.disabled = true
        refundBtn.disabled = true
        el("addr").textContent = "Phantom não detectada"
        return
    }

    connectBtn.onclick = {
        GlobalScope.promise {
            val res = provider.connect().await()
            val pk = res.publicKey
            el("addr").textContent = pk.toBase58()
            balanceBtn.disabled = false
            openEscrowBtn.disabled = false
            confirmBtn.disabled = false
            refundBtn.disabled = false
        }
        null
    }

    balanceBtn.onclick = {
        GlobalScope.promise {
            val addr = el("addr").textContent ?: return@promise
            val conn = Connection(defaultEndpoint)
            val bal = conn.getBalance(PublicKey(addr)).await().toLong()
            el("balance").textContent = bal.toString()
        }
        null
    }

    openEscrowBtn.onclick = {
        // Captura valores dos campos de entrada (stub).
        val price = (el("priceInput") as HTMLInputElement).value
        val nonce = (el("nonceInput") as HTMLInputElement).value
        el("actionStatus").textContent = "Abrindo escrow (stub): preço=" + price + ", nonce=" + nonce
    }
    confirmBtn.onclick = {
        el("actionStatus").textContent = "Confirmando entrega (stub)"
    }
    refundBtn.onclick = {
        el("actionStatus").textContent = "Solicitando refund (stub)"
    }
}