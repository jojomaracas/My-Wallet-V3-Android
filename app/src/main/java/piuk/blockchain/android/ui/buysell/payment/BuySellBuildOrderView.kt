package piuk.blockchain.android.ui.buysell.payment

import android.support.annotation.StringRes
import piuk.blockchain.android.ui.buysell.payment.models.OrderType
import piuk.blockchain.androidcoreui.ui.base.View
import piuk.blockchain.androidcoreui.ui.customviews.ToastCustom
import java.util.*

interface BuySellBuildOrderView : View {

    val locale: Locale

    val orderType: OrderType

    fun renderExchangeRate(status: BuySellBuildOrderPresenter.ExchangeRateStatus)

    fun renderSpinnerStatus(status: BuySellBuildOrderPresenter.SpinnerStatus)

    fun renderLimit(status: BuySellBuildOrderPresenter.LimitStatus)

    fun renderSellLimit(status: BuySellBuildOrderPresenter.LimitStatus)

    fun setButtonEnabled(enabled: Boolean)

    fun clearEditTexts()

    fun updateReceiveAmount(amount: String)

    fun updateSendAmount(amount: String)

    fun showToast(@StringRes message: Int, @ToastCustom.ToastType toastType: String)

    fun showQuoteInProgress(inProgress: Boolean)

    fun onFatalError()

    fun displayAccountSelector(label: String)

    fun updateAccountSelector(label: String)

}