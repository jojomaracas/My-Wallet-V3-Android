package piuk.blockchain.android.data.transactions

import info.blockchain.wallet.ethereum.data.EthTransaction
import info.blockchain.wallet.multiaddress.TransactionSummary
import piuk.blockchain.android.data.currency.CryptoCurrencies
import piuk.blockchain.android.data.ethereum.models.CombinedEthModel
import piuk.blockchain.android.util.annotations.Mockable
import java.math.BigInteger

abstract class Displayable {

    abstract val cryptoCurrency: CryptoCurrencies
    abstract val direction: TransactionSummary.Direction
    abstract val timeStamp: Long
    abstract val total: BigInteger
    abstract val fee: BigInteger
    abstract val hash: String
    abstract val inputsMap: HashMap<String, BigInteger>
    abstract val outputsMap: HashMap<String, BigInteger>
    open val confirmations = 3
    open val watchOnly: Boolean = false
    open val doubleSpend: Boolean = false
    open val isPending: Boolean = false

}

@Mockable
data class EthDisplayable(
        private val combinedEthModel: CombinedEthModel,
        private val ethTransaction: EthTransaction
) : Displayable() {

    override val cryptoCurrency: CryptoCurrencies
        get() = CryptoCurrencies.ETHER
    override val direction: TransactionSummary.Direction
        get() = when {
            combinedEthModel.getAccounts().contains(ethTransaction.from) -> TransactionSummary.Direction.SENT
            else -> TransactionSummary.Direction.RECEIVED
        }
    override val timeStamp: Long
        get() = ethTransaction.timeStamp
    override val total: BigInteger
        get() = ethTransaction.value
    override val fee: BigInteger
        get() = ethTransaction.gasUsed.multiply(ethTransaction.gasPrice)
    override val hash: String
        get() = ethTransaction.hash
    override val inputsMap: HashMap<String, BigInteger>
        get() = HashMap<String, BigInteger>().apply {
            put(ethTransaction.from, ethTransaction.value)
        }
    override val outputsMap: HashMap<String, BigInteger>
        get() = HashMap<String, BigInteger>().apply {
            put(ethTransaction.to, ethTransaction.value)
        }

}

@Mockable
data class BtcDisplayable(
        private val transactionSummary: TransactionSummary
) : Displayable() {

    override val cryptoCurrency: CryptoCurrencies
        get() = CryptoCurrencies.BTC
    override val direction: TransactionSummary.Direction
        get() = transactionSummary.direction
    override val timeStamp: Long
        get() = transactionSummary.time
    override val total: BigInteger
        get() = transactionSummary.total
    override val fee: BigInteger
        get() = transactionSummary.fee
    override val hash: String
        get() = transactionSummary.hash
    override val inputsMap: HashMap<String, BigInteger>
        get() = transactionSummary.inputsMap
    override val outputsMap: HashMap<String, BigInteger>
        get() = transactionSummary.outputsMap
    override val confirmations: Int
        get() = transactionSummary.confirmations
    override val watchOnly: Boolean
        get() = transactionSummary.isWatchOnly
    override val doubleSpend: Boolean
        get() = transactionSummary.isDoubleSpend
    override val isPending: Boolean
        get() = transactionSummary.isPending

}