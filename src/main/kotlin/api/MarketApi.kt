package api

import Auction
import Auctions
import Bids
import Util
import noSessionExists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response

object MarketApi {
    fun auctionItem(req: Request, res: Response) {
        if (req.noSessionExists()) {
            res.status(401)
            return
        }

        val session = req.session()

        val itemId = req.queryParams("item_id")
        val startingPrice = req.queryParams("starting_price")
        val marketName = req.params("name")
        val marketplace = Util.getMarketByName(marketName)

        if (marketplace == null || itemId.isNullOrBlank() || startingPrice.isNullOrBlank()) {
            res.status(400)
            return
        }

        val foundItem = Util.getItemById(itemId)

        if (foundItem == null) {
            res.status(404)
            res.redirect("/market/$marketName/auction")
            return
        }

        val username = session.attribute<String>("username")
        val user = Util.findUserByUsername(username)
        if (user == null) {
            res.status(401)
            return
        }

        val longPrice = startingPrice.toLong()

        transaction {
            if (user.balance < longPrice) {
                // they can't sell this item
                res.status(401)
                res.redirect("/market/$marketName/auction")
            }

            // user owns this item
            if (user.username == foundItem.owner.username) {
                val existingAuction = Auction.find {
                    Auctions.item eq foundItem.id
                }.firstOrNull()

                if (existingAuction != null) {
                    res.status(400)
                    res.redirect("/market/$marketName/auction")
                    return@transaction
                }

                Auctions.insert {
                    it[market] = marketplace.id
                    it[item] = foundItem.id
                }

                Bids.insert {
                    it[bidder] = user.id
                    it[amount] = startingPrice.toLong()
                    it[item] = foundItem.id
                }

                res.status(200)
                res.redirect("/market/$marketName/")
            } else {
                // they don't own this item
                res.status(401)
                res.redirect("/market/$marketName/auction")
            }
        }
    }
}