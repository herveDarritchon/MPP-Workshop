package ws

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.css.properties.boxShadow
import react.dom.*
import react.functionalComponent
import react.useEffect
import react.useState
import styled.StyleSheet
import styled.css
import styled.styledDiv
import ws.utils.getValue
import ws.utils.provideDelegate
import kotlin.browser.document
import kotlin.browser.window


private object Styles : StyleSheet("menu", isStatic = true) {
    val menu by css {
        backgroundColor = Color.white
        boxShadow(color = Color.gray, blurRadius = 6.px, spreadRadius = 1.px)
        zIndex = 1
        width = 20.em
        display = Display.flex
        flexDirection = FlexDirection.column

        "h1" {
            margin(1.em, 0.5.em)
            color = Color("#703f1c")
            textAlign = TextAlign.center
        }

        ul {
            flexGrow = 1.0
            overflow = Overflow.auto
            fontFamily = "'Varela Round', sans-serif"
            listStyleType = ListStyleType.none

            a {
                padding(0.5.em)
                display = Display.flex
                alignItems = Align.center

                span {
                    +"index" {
                        display = Display.inlineFlex
                        alignItems = Align.center
                        justifyContent = JustifyContent.center
                        color = Color.white
                        backgroundColor = Color("rgba(0, 0, 0, 0.8)")
                        borderRadius = 50.pct
                        width = 1.67.em
                        height = 1.67.em
                        fontWeight = FontWeight.bold
                        marginRight = 0.5.em
                        flexShrink = 0.0
                    }

                    +"title" {
                        display = Display.inlineBlock
                    }
                }

                +"past" {
                    span {
                        +"index" {
                            backgroundColor = Color.gray
                        }
                        +"title" {
                            color = Color.gray
                        }
                    }
                }

                +"current" {
                    fontWeight = FontWeight.bold
                    backgroundColor = Color("#00326b")

                    span {
                        +"index" {
                            color = Color("#00326b")
                            backgroundColor = Color.white
                        }

                        +"title" {
                            color = Color.white
                        }
                    }
                }

                +"future" {
                    span {
                        +"index" {
                            backgroundColor = Color("#005580")
                        }
                        +"title" {
                            color = Color("#005580")
                        }
                    }
                }

                hover {
                    +"current" {
                        cursor = Cursor.default
                    }

                    +"link" {
                        cursor = Cursor.pointer

                        span {
                            +"index" {
                                backgroundColor = Color("#078d71")
                            }

                            +"title" {
                                color = Color("#078d71")
                            }
                        }
                    }
                }
            }
        }

        div {
            +"footer" {
                display = Display.flex
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.spaceAround
                fontWeight = FontWeight.bold
                margin(2.em, 1.em)

                a {
                    borderRadius = 2.px
                    boxShadow(color = Color.gray, blurRadius = 6.px, spreadRadius = 1.px)
                    padding(0.4.em, 1.5.em)
                    cursor = Cursor.pointer

                    +"back" {
                        color = Color("#005580")
                        hover {
                            color = Color("#078d71")
                        }

                        +"start" {
                            cursor = Cursor.default
                            opacity = 0.2
                            hover {
                                color = Color("#005580")
                            }
                        }
                    }

                    +"next" {
                        color = Color.white
                        backgroundColor = Color("#005580")
                        hover {
                            backgroundColor = Color("#078d71")
                        }

                        +"end" {
                            cursor = Cursor.default
                            opacity = 0.2
                            hover {
                                backgroundColor = Color("#005580")
                            }
                        }
                    }
                }
            }
        }
    }
}

interface MenuProps : AppProps {
    var titles: Array<String>
}

val menu by functionalComponent<MenuProps> {

    styledDiv {
        css {
            +Styles.menu
        }

        h1 {
            +"Kotlin Mobile Multi-Platform"
        }

        ul {
            it.titles.forEachIndexed { index, title ->
                li {
                    a(
                            href = "#/$index",
                            classes = when {
                                index == it.index -> "current"
                                index > it.index -> "future link"
                                else -> "past link"
                            }
                    ) {
                        span("index") { +(index.toString()) }
                        span("title") { +title }
                    }
                }
            }
        }

        div("footer") {
            val c = when {
                it.index <= 0 -> "start"
                it.index >= it.titles.lastIndex -> "end"
                else -> ""
            }
            a(href = if (c != "start") "#/${it.index - 1}" else null, classes = "back $c") { +"Back" }
            a(href = if (c != "end") "#/${it.index + 1}" else null, classes = "next $c") { +"Next" }
        }
    }

}
