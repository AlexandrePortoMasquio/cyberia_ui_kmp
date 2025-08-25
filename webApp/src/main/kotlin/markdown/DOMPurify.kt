@file:JsModule("dompurify")
@file:JsNonModule

package markdown

external object DOMPurify {
    fun sanitize(dirty: String, options: dynamic = definedExternally): String
}

