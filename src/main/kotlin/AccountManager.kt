import com.hedera.sdk.account.HederaAccount
import com.hedera.sdk.common.HederaPrecheckResult
import com.hedera.sdk.common.HederaTransactionStatus
import com.hedera.sdk.common.Utilities
import com.hedera.sdk.cryptography.HederaCryptoKeyPair
import org.jetbrains.exposed.dao.EntityID

object AccountManager {
    private val _hederaAccounts = mutableMapOf<EntityID<Int>, HederaAccount>()
    val hederaAccounts: Map<EntityID<Int>, HederaAccount>
        get() = _hederaAccounts

    operator fun get(accountId: EntityID<Int>) = hederaAccounts[accountId]
    operator fun set(accountId: EntityID<Int>, account: HederaAccount) = _hederaAccounts.putIfAbsent(accountId, account)

    fun send(account: HederaAccount, target: HederaAccount, amount: Long) {

        // transfer money
        val result = account.send(target.hederaAccountID, amount)

        // check if transfer was good
        if (result?.precheckResult == HederaPrecheckResult.OK) {
            val receipt = Utilities.getReceipt(account.hederaTransactionID, account.txQueryDefaults?.node)

            if (receipt?.transactionStatus == HederaTransactionStatus.SUCCESS) {
                // TODO add receipt information to user.
            }
        }
    }

    fun create(account: HederaAccount, accountKey: HederaCryptoKeyPair, initialBalance: Long): HederaAccount? {
        val shardNum = 0L
        val realmNum = 0L

        val result = account.create(shardNum, realmNum, accountKey.publicKey, accountKey.keyType, initialBalance, null)
        if (result?.precheckResult == HederaPrecheckResult.OK) {
            val receipt = Utilities.getReceipt(account.hederaTransactionID, account.txQueryDefaults?.node)
            if (receipt?.transactionStatus == HederaTransactionStatus.SUCCESS) {
                account.accountNum = receipt.accountID.accountNum
            } else {
                return null
            }
        } else {
            return null
        }

        return account
    }
}