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
import markdown.DOMPurify
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

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
            val rawHtml = marked.parse(text)
            val cleanHtml = DOMPurify.sanitize(rawHtml)
            root.innerHTML = "<h2>${title}</h2>" + cleanHtml
        } catch (e: dynamic) {
            root.innerHTML = "<h2>${title}</h2><p>Failed to load content.</p>"
        }
    }
}

private fun renderChat() {
    appRoot().innerHTML = """
      <h2>Chat Access</h2>
      <p>Connect your wallet and check your balance. Escrow actions are stubs until the IDL is wired.</p>
      <div class="section">
        <h3>Chat</h3>
        <div class="row" style="align-items: center; gap: 0.75rem;">
          <label for="modelSelect">Model:</label>
          <select id="modelSelect">
            <option value="cyberia-small">cyberia-small</option>
            <option value="cyberia-general" selected>cyberia-general</option>
          </select>
          <button id="newChatBtn">New chat</button>
        </div>
        <div id="messages" style="min-height: 160px; padding: 0.5rem; border: 1px solid #ddd; border-radius: 8px; margin: 0.75rem 0;"></div>
        <div class="row" style="gap: 0.5rem;">
          <textarea id="chatInput" rows="3" style="flex: 1; width: 100%;"></textarea>
          <button id="sendBtn">Send</button>
        </div>
      </div>
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
    val sendBtn = el("sendBtn") as org.w3c.dom.HTMLButtonElement
    val newChatBtn = el("newChatBtn") as org.w3c.dom.HTMLButtonElement
    val chatInput = el("chatInput") as org.w3c.dom.HTMLTextAreaElement
    val messages = el("messages") as org.w3c.dom.HTMLElement
    val modelSelect = el("modelSelect") as org.w3c.dom.HTMLSelectElement

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

    fun appendMessage(role: String, text: String) {
        val safe = DOMPurify.sanitize(text)
        messages.innerHTML += """
            <div class="msg" data-role="$role" style="margin: 0.25rem 0;">
              <strong>${role}:</strong> <span>${safe}</span>
            </div>
        """.trimIndent()
        messages.scrollTop = messages.scrollHeight.toDouble()
    }

    fun clearChat() {
        messages.innerHTML = ""
        chatInput.value = ""
    }

    newChatBtn.onclick = {
        clearChat(); null
    }

    fun startMockStream(model: String, prompt: String) {
        appendMessage("user", prompt)
        val reply = when (model) {
            "cyberia-small" -> "Hello from $model. This is a concise mock reply."
            else -> "Greetings from $model. This is a longer mock response streamed token by token to simulate latency and partial output."
        }
        GlobalScope.launch {
            appendMessage("assistant", "")
            // Append to the last assistant span incrementally
            val last = messages.lastElementChild?.getElementsByTagName("span")?.item(0) as? org.w3c.dom.HTMLElement
            var i = 0
            while (i < reply.length) {
                val chunk = reply.substring(i, kotlin.math.min(i + 4, reply.length))
                last?.let { it.innerHTML = (it.innerHTML ?: "") + DOMPurify.sanitize(chunk) }
                i += 4
                delay(60)
            }
        }
    }

    fun sendCurrent() {
        val msg = chatInput.value.trim()
        if (msg.isEmpty()) return
        val model = modelSelect.value
        chatInput.value = ""
        startMockStream(model, msg)
    }

    sendBtn.onclick = { sendCurrent(); null }
    chatInput.onkeydown = {
        val ev = it as org.w3c.dom.events.KeyboardEvent
        if (ev.key == "Enter" && !ev.shiftKey) {
            ev.preventDefault()
            sendCurrent()
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
