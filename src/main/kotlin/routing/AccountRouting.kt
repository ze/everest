package routing

import Auction
import Auctions
import Util
import noSessionExists
import org.jetbrains.exposed.sql.transactions.transaction
import spark.ModelAndView
import spark.Request
import spark.Response

object AccountRouting {
    fun dashboard(req: Request, res: Response): ModelAndView? {
        return if (req.noSessionExists()) {
            res.redirect("/account/login")
            null
        } else {
            val session = req.session()
            val uname = session.attribute<String>("username")
            val user = Util.findUserByUsername(uname) ?: return null

            val itemsOwnedByUser = Util.getItemsOwnedByUser(user).filter {
                transaction {
                    // if item is being sold elsewhere, don't have it listed
                    Auction.find {
                        Auctions.item eq it.id
                    }.firstOrNull() ?: return@transaction true

                    false
                }
            }

            val model = mapOf(
                    "not_auth" to req.noSessionExists(),
                    "items_owned" to itemsOwnedByUser,
                    "user" to user
            )
            ModelAndView(model, "account/index.ftl")
        }
    }

    fun login(req: Request, res: Response): ModelAndView? {
        return if (req.noSessionExists()) {
            val model = mutableMapOf<String, Any>("not_auth" to req.noSessionExists())
            ModelAndView(model, "account/login.ftl")
        } else {
            res.redirect("/account/")
            null
        }
    }

    fun register(req: Request, res: Response): ModelAndView? {
        return if (req.noSessionExists()) {
            val model = mutableMapOf<String, Any>("not_auth" to req.noSessionExists())
            ModelAndView(model, "account/register.ftl")
        } else {
            res.redirect("/account/")
            null
        }
    }
}