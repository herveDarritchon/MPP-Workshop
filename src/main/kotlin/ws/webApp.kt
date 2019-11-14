package ws

import react.RProps
import react.functionalComponent
import react.router.dom.hashRouter
import react.router.dom.redirect
import react.router.dom.route
import react.router.dom.switch
import ws.utils.fchild
import ws.utils.getValue
import ws.utils.invoke


interface RouteProps : RProps {
    var index: String?
}

val webApp by functionalComponent<RProps> {
    hashRouter {
        switch {
            route<RouteProps>("/:index") {
                fchild(app {
                    index = it.match.params.index?.toIntOrNull() ?: -1
                })
            }

            redirect(from = "/", to = "/0")
        }
    }
}
