package ws

import react.dom.render
import ws.utils.fchild
import kotlin.browser.document


fun main() {
    render(document.getElementById("app")) {
        fchild(webApp)
    }
}
