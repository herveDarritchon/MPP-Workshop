package ws

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.css.properties.boxShadow
import kotlinx.html.classes
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.asList
import org.w3c.dom.get
import react.dom.span
import react.functionalComponent
import react.useEffect
import react.useRef
import styled.css
import styled.styledDiv
import ws.utils.getValue
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.clear

private external val hljs: dynamic

interface PageProps : AppProps {
    var titles: Array<String>
}

val page by functionalComponent<PageProps> {

    val div = useRef<HTMLDivElement?>(null)

    useEffect(listOf(it.index)) {
        GlobalScope.launch {
            val response = window.fetch("${it.index}.html").await()
            if (response.ok.not()) {
                window.alert("Could not get page ${it.index} : ${response.statusText}")
                return@launch
            }
            val html = document.createElement("html")
            html.innerHTML = response.text().await()
            val body = html.getElementsByTagName("body")[0]!!

            val links = body.getElementsByTagName("a")
            for (i in 0 until links.length) {
                val link = links[i]!! as HTMLAnchorElement
                val attr = link.attributes["href"] ?: continue
                if (attr.value.startsWith("#")) attr.value = "#/${it.index}/${attr.value.substring(1)}"
                else link.target = "_blank"
            }

            div.current!!.clear()
            while (body.children.length > 0) div.current!!.appendChild(body.children[0]!!)

            div.current!!.querySelectorAll("pre code").asList().forEach {
                hljs.highlightBlock(it)
            }

            if (it.index < it.titles.lastIndex) {
                div.current!!.appendChild(document.createElement("div").apply {
                    className = "workshop-next"
                    innerHTML = "<a href='#/${it.index + 1}'><span>${it.index + 1}</span>${it.titles[it.index + 1]}</a>"
                })
            }

            val anchor = it.anchor
            if (anchor != null)
                div.current!!.querySelector("#$anchor")?.scrollIntoView()
            else
                div.current!!.scrollTop = 0.0
        }
    }

    useEffect(listOf(it.anchor)) {
        it.anchor?.let { anchor -> document.getElementById(anchor)?.scrollIntoView() }
    }

    styledDiv {
        css {
            height = 100.pct
            overflow = Overflow.auto

            "div.workshop-next" {
                maxWidth = 970.px
                margin(LinearDimension.auto)
                display = Display.flex
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.flexEnd

                a {
                    maxWidth = 21.em
                    backgroundColor = Color("#005580")
                    color = Color.white
                    boxShadow(color = Color.gray, blurRadius = 6.px, spreadRadius = 1.px)
                    borderRadius = 2.px
                    padding(0.5.em, 1.em)
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    alignItems = Align.center
                    margin(0.em, 1.em, 2.em, 0.em)
                    fontFamily = "'Varela Round', sans-serif"
                    fontWeight = FontWeight.bold

                    "span" {
                        display = Display.inlineFlex
                        alignItems = Align.center
                        justifyContent = JustifyContent.center
                        color = Color("#005580")
                        backgroundColor = Color.white
                        borderRadius = 50.pct
                        width = 1.67.em
                        height = 1.67.em
                        fontWeight = FontWeight.bold
                        marginRight = 0.5.em
                        flexShrink = 0.0
                    }

                    hover {
                        backgroundColor = Color("#078d71")
                        "span" {
                            color = Color("#078d71")
                        }
                    }

                }
            }
        }

        attrs {
            classes = setOf("article")
        }
        ref = div
    }

}
