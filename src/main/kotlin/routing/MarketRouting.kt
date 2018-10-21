package routing

import Auction
import Auctions
import Bid
import Bids
import Item
import Items
import Market
import User
import Users
import Util
import noSessionExists
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import spark.ModelAndView
import spark.Request
import spark.Response

object MarketRouting {
    fun viewAllMarkets(req: Request, res: Response): ModelAndView? {
        return if (req.noSessionExists()) {
            res.redirect("/account/login")
            null
        } else {
            val model = mutableMapOf<String, Any>("not_auth" to req.noSessionExists())

            val markets = transaction {
                Market.all().toList().map {
                    it to Util.getAuctionsForMarket(it).count()
                }
            }
            model["markets"] = markets

            ModelAndView(model, "marketplace.ftl")
        }
    }

    fun viewMarket(req: Request, res: Response): ModelAndView? {
        if (req.noSessionExists()) {
            res.redirect("/account/login")
            return null
        }

        val name = req.params("name")
        val market = Util.getMarketByName(name)
        if (market == null) {
            res.redirect("/market/")
            return null
        }

        val auctions = transaction {
            Util.getAuctionsForMarket(market).map {
                it.item
            }.map {
                it to Util.getHighestBidderInformation(it).let { pair ->
                    if (pair.first == null) {
                        return@let null
                    } else {
                        return@let pair
                    }
                }
            }
        }

        val model = mapOf(
                "not_auth" to req.noSessionExists(),
                "name" to market.name,
                "items_info" to auctions
        )

        return ModelAndView(model, "/market/index.ftl")
    }

    fun auctionItem(req: Request, res: Response): ModelAndView? {
        if (req.noSessionExists()) {
            res.redirect("/account/login")
            return null
        }

        val name = req.params("name")
        val market = Util.getMarketByName(name)
        if (market == null) {
            res.redirect("/market/")
            return null
        }

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
                "name" to market.name,
                "items_owned" to itemsOwnedByUser,
                "is_auction_form" to true
        )
        return ModelAndView(model, "/market/auction.ftl")
    }

    fun viewItem(req: Request, res: Response): ModelAndView? {
        if (req.noSessionExists()) {
            res.redirect("/account/login")
            return null
        }

        val name = req.params("name")
        val market = Util.getMarketByName(name)
        if (market == null) {
            res.redirect("/market/")
            return null
        }

        val itemId = req.params("item_id").toIntOrNull()
        if (itemId == null) {
            res.redirect("/market/$name/")
            return null
        }

        val item = transaction {
            val found = Item.find {
                Items.id eq itemId
            }.firstOrNull() ?: return@transaction null

            Auction.find {
                Auctions.market eq market.id and (Auctions.item eq found.id)
            }.firstOrNull() ?: return@transaction null

            found
        }

        if (item == null) {
            res.redirect("/market/$name/")
            return null
        }

        val bidsPair = transaction {
            Bid.find {
                Bids.item eq item.id
            }.map {
                it to User.find {
                    Users.id eq it.bidder.id
                }.first()
            }.toList()
        }

        val session = req.session()
        val username = session.attribute<String>("username")
        val user = Util.findUserByUsername(username)

        val isOurItem = transaction { user?.id == item.owner.id }

        val model = mapOf(
                "not_auth" to req.noSessionExists(),
                "name" to market.name,
                "item" to item,
                "bids_pair" to bidsPair,
                "is_our_item" to isOurItem
        )
        return ModelAndView(model, "/market/view.ftl")
    }

    fun bidOnItem(req: Request, res: Response): ModelAndView? {
        if (req.noSessionExists()) {
            res.redirect("/account/login")
            return null
        }

        val name = req.params("name")
        val market = Util.getMarketByName(name)
        if (market == null) {
            res.redirect("/market/")
            return null
        }

        val itemId = req.params("item_id")
        if (itemId == null) {
            res.redirect("/market/$name/")
            return null
        }

        val item = transaction {
            val found = Util.getItemById(itemId) ?: return@transaction null

            Auction.find {
                Auctions.market eq market.id and (Auctions.item eq found.id)
            }.firstOrNull() ?: return@transaction null

            found
        }

        if (item == null) {
            res.redirect("/market/$name/")
            return null
        }

        val bidsPair = transaction {
            Bid.find {
                Bids.item eq item.id
            }.map {
                it to User.find {
                    Users.id eq it.bidder.id
                }.first() // can this be null?
            }.toList()
        }

        val session = req.session()
        val username = session.attribute<String>("username")
        val user = Util.findUserByUsername(username)

        val isOurItem = transaction { user?.id == item.owner.id }

        val model = mapOf(
                "not_auth" to req.noSessionExists(),
                "name" to market.name,
                "item" to item,
                "user" to user,
                "bids_pair" to bidsPair,
                "is_our_item" to isOurItem
        )

        return ModelAndView(model, "/market/bid.ftl")
    }
}