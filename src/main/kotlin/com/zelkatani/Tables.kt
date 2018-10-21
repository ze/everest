package com.zelkatani

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Markets : IntIdTable() {
    val name = varchar("name", 50)
}

object Auctions : IntIdTable() {
    val market = reference("market", Markets)
    val item = reference("item", Items)
}

object Items : IntIdTable() {
    val name = varchar("name", 50)
    val owner = reference("owner", Users)
}

object Bids : IntIdTable() {
    val bidder = reference("bidder", Users)
    val amount = long("amount")
    val item = reference("item", Items)
}

object Users : IntIdTable() {
    val username = varchar("username", 20)
    val password = varchar("password", 100)
    val salt = varchar("salt", 100)
    val balance = long("balance")
}

class Market(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Market>(Markets)

    var name by Markets.name
}

class Auction(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Auction>(Auctions)

    var market by Market referencedOn Auctions.market
    var item by Item referencedOn Auctions.item
}

class Item(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Item>(Items)

    var name by Items.name
    var owner by User referencedOn Items.owner
}

class Bid(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Bid>(Bids)

    var bidder by User referencedOn Bids.bidder
    var amount by Bids.amount
    var item by Item referencedOn Bids.item
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var username by Users.username
    var password by Users.password
    var salt by Users.salt
    var balance by Users.balance
}
