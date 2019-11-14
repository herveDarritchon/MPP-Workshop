package ws

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
import react.RProps
import react.functionalComponent
import react.useEffect
import react.useState
import styled.css
import styled.styledDiv
import ws.utils.*
import kotlin.browser.window


interface AppProps : RProps {
    var index: Int
    var anchor: String?
}

val app by functionalComponent<AppProps> {

    var titles by useState { emptyArray<String>() }

    useEffect(emptyList()) {
        GlobalScope.launch {
            val response = window.fetch("pages.json").await()
            if (response.ok.not()) {
                window.alert("Could not get pages JSON: ${response.statusText}")
                return@launch
            }
            val newTitles = response.json().await()
            @Suppress("UNCHECKED_CAST")
            titles = newTitles as Array<String>
        }
    }

    useEffect(listOf(it.index, titles)) {
        if (titles.isNotEmpty() && (it.index < 0 || it.index > titles.lastIndex)) {
            window.location.href = "#/0"
        }
    }

    styledDiv {
        css {
            fontFamily = "'Open Sans', sans-serif"

            "h1" {
                fontFamily = "'Varela Round', sans-serif"
            }

            a {
                textDecoration = TextDecoration.none
            }

            display = Display.flex
            flexDirection = FlexDirection.row
            height = 100.pct
        }

        fchild(menu {
            this.index = it.index
            this.titles = titles
        })

        styledDiv {
            css {
                flex(1.0)
                width = LinearDimension("calc(100% - 20em)")
            }
            if (titles.isNotEmpty()) {
                fchild(page {
                    this.index = it.index
                    this.anchor = it.anchor
                    this.titles = titles
                })
            }
        }
    }
}
