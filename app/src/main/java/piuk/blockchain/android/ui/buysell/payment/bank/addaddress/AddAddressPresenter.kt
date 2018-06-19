package piuk.blockchain.android.ui.buysell.payment.bank.addaddress

import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import piuk.blockchain.android.R
import piuk.blockchain.android.util.extensions.addToCompositeDisposable
import piuk.blockchain.androidbuysell.datamanagers.BuyDataManager
import piuk.blockchain.androidbuysell.datamanagers.CoinifyDataManager
import piuk.blockchain.androidbuysell.models.coinify.Account
import piuk.blockchain.androidbuysell.models.coinify.Address
import piuk.blockchain.androidbuysell.models.coinify.Bank
import piuk.blockchain.androidbuysell.models.coinify.BankAccount
import piuk.blockchain.androidbuysell.models.coinify.Holder
import piuk.blockchain.androidbuysell.models.coinify.exceptions.CoinifyApiException
import piuk.blockchain.androidbuysell.services.ExchangeService
import piuk.blockchain.androidcore.utils.extensions.applySchedulers
import piuk.blockchain.androidcore.utils.helperfunctions.unsafeLazy
import piuk.blockchain.androidcoreui.ui.base.BasePresenter
import piuk.blockchain.androidcoreui.ui.customviews.ToastCustom
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class AddAddressPresenter @Inject constructor(
        private val coinifyDataManager: CoinifyDataManager,
        private val exchangeService: ExchangeService,
        private val buyDataManager: BuyDataManager
) : BasePresenter<AddAddressView>() {

    private val countryCodeMap by unsafeLazy {
        Locale.getISOCountries().associateBy(
                { Locale("en", it).displayCountry },
                { it }
        ).toSortedMap()
    }

    private val tokenSingle: Single<String>
        get() = exchangeService.getExchangeMetaData()
                .addToCompositeDisposable(this)
                .applySchedulers()
                .singleOrError()
                .map { it.coinify!!.token }

    override fun onViewReady() {
        setCountryCodeMap()

        buyDataManager.countryCode
                .applySchedulers()
                .addToCompositeDisposable(this)
                .subscribeBy(onNext = { autoSelectCountry(it) })
    }

    internal fun onConfirmClicked() {
        if (!isDataValid()) return

        tokenSingle.flatMap { token ->
            coinifyDataManager.getTrader(token)
                    .flatMap {
                        coinifyDataManager.addBankAccount(
                                token,
                                BankAccount(
                                        account = Account(
                                                it.defaultCurrency,
                                                null,
                                                view.bic,
                                                view.iban
                                        ),
                                        bank = Bank(
                                                address = Address(
                                                        countryCode = getCountryCodeFromPosition(
                                                                view.countryCodePosition
                                                        )
                                                )
                                        ),
                                        holder = Holder(
                                                view.accountHolderName, Address(
                                                street = view.streetAndNumber,
                                                zipcode = view.postCode,
                                                city = view.city,
                                                countryCode = getCountryCodeFromPosition(view.countryCodePosition)
                                        )
                                        )
                                )
                        )
                    }
        }.doOnSubscribe { view.showProgressDialog() }
                .doOnEvent { _, _ -> view.dismissProgressDialog() }
                .doOnError { Timber.e(it) }
                .subscribeBy(
                        onSuccess = { view.goToConfirmation() },
                        onError = {
                            if (it is CoinifyApiException) {
                                view.showErrorDialog(it.getErrorDescription())
                            } else {
                                view.showToast(R.string.unexpected_error, ToastCustom.TYPE_ERROR)
                            }
                        }
                )
    }

    private fun isDataValid(): Boolean {
        require(!view.iban.isEmpty()) { }
        require(!view.bic.isEmpty()) { }

        if (view.accountHolderName.isEmpty()) {
            view.showToast(R.string.buy_sell_add_address_name_empty, ToastCustom.TYPE_ERROR)
            return false
        }

        if (view.streetAndNumber.isEmpty()) {
            view.showToast(R.string.buy_sell_add_address_street_empty, ToastCustom.TYPE_ERROR)
            return false
        }

        if (view.city.isEmpty()) {
            view.showToast(R.string.buy_sell_add_address_city_empty, ToastCustom.TYPE_ERROR)
            return false
        }

        if (view.postCode.isEmpty()) {
            view.showToast(R.string.buy_sell_add_address_postcode_empty, ToastCustom.TYPE_ERROR)
            return false
        }

        return true
    }

    private fun setCountryCodeMap() {
        view.setCountryPickerData(countryCodeMap.keys.toList())
    }

    private fun autoSelectCountry(countryCode: String) {
        val countryName = countryCodeMap
                .filterValues { it == countryCode }.keys
                .firstOrNull() ?: ""

        if (countryName.isNotEmpty()) {
            view.onAutoSelectCountry(countryCodeMap.keys.indexOf(countryName))
        }
    }

    private fun getCountryCodeFromPosition(countryPosition: Int): String {
        val countryName =
                countryCodeMap.keys.filterIndexed { index, _ -> index == countryPosition }.last()
        return countryCodeMap[countryName]!!
    }
}