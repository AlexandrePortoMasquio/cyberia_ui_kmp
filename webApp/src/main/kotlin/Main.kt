import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlinx.coroutines.await
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLElement
import solana.Connection
import solana.PublicKey
import js.PhantomProvider
import js.window as jsWindow
import markdown.marked

/**
 * Kotlin/JS SPA entrypoint. Provides navigation between content pages
 * and the Chat page (which wires wallet and escrow stubs).
 */
private val defaultEndpoint: String = js("(typeof process !== 'undefined' && process.env.SOLANA_RPC) || ''") as? String?
    ?: "https://api.devnet.solana.com"

private fun el(id: String) = document.getElementById(id)!!
private fun appRoot(): HTMLElement = document.getElementById("app") as HTMLElement

fun main() {
    fun route() {
        val hash = window.location.hash.removePrefix("#").ifEmpty { "/home" }
        when (hash) {
            "/home" -> renderHome()
            "/whitepaper" -> renderWhitepaper()
            "/philosophy" -> renderPhilosophy()
            "/token" -> renderToken()
            "/chat" -> renderChat()
            "/roadmap" -> renderRoadmap()
            "/legal" -> renderLegal()
            else -> renderNotFound()
        }
    }
    window.onhashchange = { route(); null }
    route()
}

private fun renderHome() {
    appRoot().innerHTML = """
        <h2>Welcome to Cyberia (XCYB)</h2>
        <p>Explore our philosophy, whitepaper, and token. Chat with our decentralized agents and pay using XCYB via on-chain escrow.</p>
        <p>Use the navigation above to get started.</p>
    """.trimIndent()
}

private fun renderWhitepaper() {
    renderMarkdownPage("content/whitepaper.md", "Whitepaper")
}

private fun renderPhilosophy() {
    renderMarkdownPage("content/philosophy.md", "Philosophy")
}

private fun renderToken() {
    appRoot().innerHTML = """
        <h2>Token (XCYB)</h2>
        <ul>
          <li>Mint: <code id="mint-xcyb">Configured in idl/config.json</code></li>
          <li>Supply: TBD</li>
          <li>Utility: Escrow payments for chatbot access</li>
        </ul>
    """.trimIndent()
}

private fun renderRoadmap() {
    renderMarkdownPage("content/roadmap.md", "Roadmap")
}

private fun renderLegal() {
    renderMarkdownPage("content/legal.md", "Legal")
}

private fun renderNotFound() {
    appRoot().innerHTML = """
        <h2>Not Found</h2>
        <p>The page you requested does not exist.</p>
    """.trimIndent()
}

private fun renderMarkdownPage(path: String, title: String) {
    val root = appRoot()
    root.innerHTML = "<h2>${title}</h2><p>Loading…</p>"
    GlobalScope.promise {
        try {
            val resp = window.fetch(path).await()
            val text = resp.text().await()
            val html = marked.parse(text)
            root.innerHTML = "<h2>${title}</h2>" + html
        } catch (e: dynamic) {
            root.innerHTML = "<h2>${title}</h2><p>Failed to load content.</p>"
        }
    }
}

private fun renderChat() {
    appRoot().innerHTML = """
      <h2>Chat Access</h2>
      <p>Connect your wallet and check your balance. Escrow actions are stubs until the IDL is wired.</p>
      <div class="row">
        <button id="connectBtn" data-testid="connect">Connect wallet</button>
        <button id="balanceBtn" data-testid="balance" disabled>Get balance</button>
      </div>
      <div class="status">
        Address: <span id="addr" data-testid="address">—</span>
      </div>
      <div class="status">
        Balance (lamports): <span id="balance" data-testid="lamports">—</span>
      </div>
      <div class="section">
        <h3>Escrow Actions</h3>
        <div class="row">
          <label for="priceInput">Price (lamports):</label>
          <input id="priceInput" data-testid="price" type="number" min="0" />
          <label for="nonceInput">Nonce:</label>
          <input id="nonceInput" data-testid="nonce" type="number" min="0" />
        </div>
        <div class="row">
          <button id="openEscrowBtn" data-testid="open" disabled>Open escrow</button>
          <button id="confirmBtn" data-testid="confirm" disabled>Confirm delivery</button>
          <button id="refundBtn" data-testid="refund" disabled>Refund (timeout)</button>
        </div>
        <div class="status" id="actionStatus" data-testid="status"></div>
      </div>
    """.trimIndent()

    val connectBtn = el("connectBtn") as org.w3c.dom.HTMLButtonElement
    val balanceBtn = el("balanceBtn") as org.w3c.dom.HTMLButtonElement
    val openEscrowBtn = el("openEscrowBtn") as org.w3c.dom.HTMLButtonElement
    val confirmBtn = el("confirmBtn") as org.w3c.dom.HTMLButtonElement
    val refundBtn = el("refundBtn") as org.w3c.dom.HTMLButtonElement

    val provider = jsWindow.asDynamic().solana as? PhantomProvider
    if (provider == null || provider.isPhantom != true) {
        connectBtn.isDisabled(true)
        balanceBtn.isDisabled(true)
        openEscrowBtn.isDisabled(true)
        confirmBtn.isDisabled(true)
        refundBtn.isDisabled(true)
        el("addr").textContent = "Phantom not detected"
        return
    }

    connectBtn.onclick = {
        GlobalScope.promise {
            val res = provider.connect().await()
            val pk = res.publicKey
            el("addr").textContent = pk.toBase58()
            balanceBtn.isDisabled(false)
            openEscrowBtn.isDisabled(false)
            confirmBtn.isDisabled(false)
            refundBtn.isDisabled(false)
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
        val price = (el("priceInput") as HTMLInputElement).value
        val nonce = (el("nonceInput") as HTMLInputElement).value
        el("actionStatus").textContent = "Opening escrow (stub): price=" + price + ", nonce=" + nonce
    }
    confirmBtn.onclick = {
        el("actionStatus").textContent = "Confirming delivery (stub)"
    }
    refundBtn.onclick = {
        el("actionStatus").textContent = "Requesting refund (stub)"
    }
}

private fun org.w3c.dom.HTMLButtonElement.isDisabled(v: Boolean) {
    this.disabled = v
}
