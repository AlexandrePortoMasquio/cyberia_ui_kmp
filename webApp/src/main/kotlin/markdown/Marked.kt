@file:JsModule("marked")
@file:JsNonModule

package markdown

external object marked {
    fun parse(src: String): String
}

