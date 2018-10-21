package api

import Auctions
import Bid
import Bids
import Items
import Users
import Util
import noSessionExists
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import spark.Request
import spark.Response

object ItemApi {
    fun bid(req: Request, res: Response) {
        if (req.noSessionExists()) {
            res.status(401)
            return
        }

        val marketName = req.params("name")
        val itemId = req.params("item_id")
        val bidAmount = req.queryParams("bid_amount")

        if (marketName.isNullOrBlank() || itemId.isNullOrBlank() || bidAmount.isNullOrBlank()) {
            res.status(400)
            return
        }

        val market = Util.getMarketByName(marketName)
        if (market == null) {
            res.redirect("/market/")
            return
        }

        val foundItem = Util.getItemById(itemId)
        if (foundItem == null) {
            res.status(404)
            res.redirect("/market/$marketName/auction")
            return
        }

        val session = req.session()
        val username = session.attribute<String>("username")
        val user = Util.findUserByUsername(username) ?: return

        val bidAmountLong = bidAmount.toLong()
        val highestBidder = Util.getHighestBidderInformation(foundItem)
        val highestBidderAmount = highestBidder.first ?: return

        if (bidAmountLong > highestBidderAmount) {
            transaction {
                Bid.new {
                    amount = bidAmountLong
                    bidder = user
                    item = foundItem
                }
            }

            res.status(200)
            res.redirect("/market/$marketName/view/$itemId")
        } else {
            res.redirect("/market/$marketName/view/$itemId/bid")
        }
    }

    fun sell(req: Request, res: Response) {
        if (req.noSessionExists()) {
            res.status(401)
            return
        }

        val marketName = req.params("name")
        val itemId = req.params("item_id")
        if (marketName.isNullOrBlank() || itemId.isNullOrBlank()) {
            res.status(400)
            return
        }

        val market = Util.getMarketByName(marketName)
        if (market == null) {
            res.status(404)
            res.redirect("/market/")
            return
        }

        val foundItem = Util.getItemById(itemId)
        if (foundItem == null) {
            res.status(404)
            res.redirect("/market/$marketName/auction")
            return
        }

        val session = req.session()
        val username = session.attribute<String>("username")
        val user = Util.findUserByUsername(username) ?: return

        val highestBidderInformation = Util.getHighestBidderInformation(foundItem)
        val transferAmount = highestBidderInformation.first!!
        val transferUser = highestBidderInformation.second!!

        transaction {
            val userBalance = user.balance + transferAmount
            val transferBalance = transferUser.balance - transferAmount

            Users.update({ Users.id eq user.id }) {
                it[balance] = userBalance
            }

            Users.update({ Users.id eq transferUser.id }) {
                it[balance] = transferBalance
            }

            Items.update({ Items.id eq foundItem.id }) {
                it[owner] = transferUser.id
            }

            Bids.deleteWhere {
                Bids.item eq foundItem.id
            }

            Auctions.deleteWhere {
                Auctions.item eq foundItem.id
            }
        }

        res.redirect("/market/$marketName/")
    }
}