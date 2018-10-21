package com.zelkatani

import com.zelkatani.api.AccountApi
import com.zelkatani.api.ItemApi
import com.zelkatani.api.MarketApi
import com.zelkatani.routing.AccountRouting
import com.zelkatani.routing.IndexRouting
import com.zelkatani.routing.MarketRouting
import org.apache.log4j.BasicConfigurator
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Spark.*
import spark.template.freemarker.FreeMarkerEngine

fun main(args: Array<String>) {
    prepareDatabase()
    startMainServer()
}

fun prepareDatabase() {
    Database.connect(
            "jdbc:mysql://127.0.0.1:3306/everest?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false",
            driver = "com.mysql.cj.jdbc.Driver",
            user = System.getenv("EVEREST_SERVER_USER"),
            password = System.getenv("EVEREST_SERVER_PASSWORD")
    )

    BasicConfigurator.configure()
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Markets, Auctions, Items, Bids, Users)

        if (Market.find { Markets.name eq "Central" }.firstOrNull() == null) {
            Markets.insertIgnore {
                it[name] = "Central"
            }
        }
    }
}

fun startMainServer() {
    port(8000)
    staticFiles.location("static")

    val engine = FreeMarkerEngine()

    get("/", IndexRouting::index, engine)

    path("/account") {
        get("/", AccountRouting::dashboard, engine)
        get("/login", AccountRouting::login, engine)
        get("/register", AccountRouting::register, engine)
    }

    path("/market") {
        get("/", MarketRouting::viewAllMarkets, engine)
        path("/:name") {
            get("/", MarketRouting::viewMarket, engine)
            get("/auction", MarketRouting::auctionItem, engine)

            get("/view/:item_id", MarketRouting::viewItem, engine)
            get("/view/:item_id/bid", MarketRouting::bidOnItem, engine)
        }
    }

    path("/api") {
        path("/market/:name") {
            post("/auction", MarketApi::auctionItem)

            path("/item/:item_id") {
                post("/bid", ItemApi::bid)
                post("/sell", ItemApi::sell)
            }
        }

        path("/account") {
            post("/login", AccountApi::login)
            post("/logout", AccountApi::logout)
            post("/register", AccountApi::register)
        }
    }
}