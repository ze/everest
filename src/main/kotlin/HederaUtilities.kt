import com.hedera.sdk.common.HederaAccountID
import com.hedera.sdk.common.HederaDuration
import com.hedera.sdk.common.HederaKey.KeyType
import com.hedera.sdk.common.HederaTransactionAndQueryDefaults
import com.hedera.sdk.cryptography.HederaCryptoKeyPair
import com.hedera.sdk.node.HederaNode
import java.io.IOException
import java.io.InputStream
import java.security.spec.InvalidKeySpecException
import java.util.*

object HederaUtilities {
    var nodeAddress = ""
    var nodePort = 0
    var nodeAccountShard: Long = 0
    var nodeAccountRealm: Long = 0
    var nodeAccountNum: Long = 0
    var pubKey = ""
    var privKey = ""
    var payAccountShard: Long = 0
    var payAccountRealm: Long = 0
    var payAccountNum: Long = 0

    // Get node details
    // setup node account ID
    // setup node
    // setup paying account
    // setup paying keypair
    // setup a set of defaults for query and transactions
    val txQueryDefaults: HederaTransactionAndQueryDefaults
        @Throws(InvalidKeySpecException::class)
        get() {
            HederaUtilities.getNodeDetails()
            val nodeAccountID = HederaAccountID(HederaUtilities.nodeAccountShard, HederaUtilities.nodeAccountRealm, HederaUtilities.nodeAccountNum)
            val node = HederaNode(HederaUtilities.nodeAddress, HederaUtilities.nodePort, nodeAccountID)
            val payingAccountID = HederaAccountID(HederaUtilities.payAccountShard, HederaUtilities.payAccountRealm, HederaUtilities.payAccountNum)
            val payingKeyPair = HederaCryptoKeyPair(KeyType.ED25519, HederaUtilities.pubKey, HederaUtilities.privKey)
            val txQueryDefaults = HederaTransactionAndQueryDefaults()

            txQueryDefaults.memo = "Demo memo"
            txQueryDefaults.node = node
            txQueryDefaults.payingAccountID = payingAccountID
            txQueryDefaults.payingKeyPair = payingKeyPair
            txQueryDefaults.transactionValidDuration = HederaDuration(120, 0)

            return txQueryDefaults
        }

    fun getNodeDetails() {

        // load application properties
        val applicationProperties = Properties()
        var propertiesInputStream: InputStream? = null

        try {
            propertiesInputStream = javaClass.classLoader.getResourceAsStream("node.properties")

            // load a properties file
            applicationProperties.load(propertiesInputStream)

            // get the property value and print it out
            nodeAddress = applicationProperties.getProperty("nodeaddress")
            nodePort = Integer.parseInt(applicationProperties.getProperty("nodeport"))

            nodeAccountShard = java.lang.Long.parseLong(applicationProperties.getProperty("nodeAccountShard"))
            nodeAccountRealm = java.lang.Long.parseLong(applicationProperties.getProperty("nodeAccountRealm"))
            nodeAccountNum = java.lang.Long.parseLong(applicationProperties.getProperty("nodeAccountNum"))

            pubKey = applicationProperties.getProperty("pubkey")
            privKey = applicationProperties.getProperty("privkey")

            payAccountShard = java.lang.Long.parseLong(applicationProperties.getProperty("payingAccountShard"))
            payAccountRealm = java.lang.Long.parseLong(applicationProperties.getProperty("payingAccountRealm"))
            payAccountNum = java.lang.Long.parseLong(applicationProperties.getProperty("payingAccountNum"))

        } catch (ex: IOException) {
            ex.printStackTrace()
            System.exit(1)
        } finally {
            if (propertiesInputStream != null) {
                try {
                    propertiesInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

}