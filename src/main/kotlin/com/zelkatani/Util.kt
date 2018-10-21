package com.zelkatani

import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request

object Util {
    fun findUserByUsername(name: String) = transaction {
        User.find {
            Users.username eq name
        }.firstOrNull()
    }

    fun getMarketByName(marketName: String) = transaction {
        Market.find {
            Markets.name eq marketName
        }.firstOrNull()
    }

    fun getAuctionsForMarket(market: Market) = transaction {
        Auction.find {
            Auctions.market eq market.id
        }
    }

    fun getBidsForItem(item: Item) = transaction {
        Bid.find {
            Bids.item eq item.id
        }.toList()
    }

    fun getHighestBidderInformation(item: Item) = transaction {
        val max = getBidsForItem(item).maxBy {
            it.amount
        }

        Pair(max?.amount, max?.bidder)
    }

    fun getItemsOwnedByUser(user: User) = transaction {
        Item.find {
            Items.owner eq user.id
        }.toList()
    }

    fun getItemById(itemId: String) = transaction {
        Item.find {
            Items.id eq itemId.toIntOrNull()
        }.firstOrNull()
    }
}

fun Request.noSessionExists() = session(false) == null
