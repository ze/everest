package api

import AccountManager
import HederaUtilities
import Items
import User
import Users
import Util
import com.hedera.sdk.account.HederaAccount
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import spark.Request
import spark.Response
import java.io.File
import java.util.concurrent.ThreadLocalRandom


object AccountApi {
    private val objects = File(javaClass.classLoader.getResource("objects.txt").file).readLines()

    fun login(req: Request, res: Response) {
        val username = req.queryUsername() ?: return
        val password = req.queryPassword() ?: return

        val authUser = authenticate(username, password)
        if (authUser != null) {
            val session = req.session(true)
            session.attribute("username", username)
            session.attribute("hedera", AccountManager.hederaAccounts[authUser.id])

            res.redirect("/market/")
        } else {
            res.redirect("/account/login")
        }
    }

    fun logout(req: Request, res: Response) {
        val session = req.session(false)
        session?.invalidate()
        res.redirect("/")
    }

    fun register(req: Request, res: Response) {
        val uname = req.queryUsername()
        val pass = req.queryPassword()

        if (uname.isNullOrBlank() || pass.isNullOrBlank()) {
            res.redirect("/account/register")
            return
        }

        val existingUser = Util.findUserByUsername(uname)
        if (existingUser == null) {
            val saltGen = BCrypt.gensalt()
            val hashedPassword = BCrypt.hashpw(pass, saltGen)
            val startingBalance = 10000L

            val txQueryDefaults = HederaUtilities.txQueryDefaults
            val hederaAccount = HederaAccount()
            hederaAccount.txQueryDefaults = txQueryDefaults
            hederaAccount.txQueryDefaults.generateRecord = false
            hederaAccount.initialBalance = 10000L

            transaction {
                val id = Users.insertAndGetId {
                    it[username] = uname
                    it[salt] = saltGen
                    it[password] = hashedPassword
                    it[balance] = startingBalance
                }

                AccountManager[id] = hederaAccount

                val items = takeTenRandomObjects()
                items.forEach { str ->
                    Items.insert {
                        it[name] = str
                        it[owner] = id
                    }
                }
            }

            res.redirect("/account/login")
        } else {
            res.redirect("/account/register")
        }
    }

    private fun takeTenRandomObjects(): List<String> {
        val list = mutableListOf<String>()

        repeat(10) {
            val randomNumber = ThreadLocalRandom.current().nextInt(objects.size)
            list.add(objects[randomNumber])
        }

        return list
    }

    private fun authenticate(username: String, password: String): User? {
        if (username.isBlank() || password.isBlank()) {
            return null
        }

        val user = Util.findUserByUsername(username) ?: return null
        val hashed = BCrypt.hashpw(password, user.salt)

        return if (hashed?.equals(user.password) == true) user else {
            null
        }
    }

    private fun Request.queryUsername() = queryParams("username")
    private fun Request.queryPassword() = queryParams("password")
}