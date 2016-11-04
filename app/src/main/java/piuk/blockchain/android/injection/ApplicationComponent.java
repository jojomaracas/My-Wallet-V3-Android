package piuk.blockchain.android.injection;

import javax.inject.Singleton;

import dagger.Component;
import piuk.blockchain.android.ui.receive.ReceiveCurrencyHelper;
import piuk.blockchain.android.util.AppUtil;
import piuk.blockchain.android.util.ExchangeRateFactory;
import piuk.blockchain.android.util.exceptions.LoggingExceptionHandler;

/**
 * Created by adambennett on 08/08/2016.
 */

@SuppressWarnings("WeakerAccess")
@Singleton
@Component(modules = {
        ApplicationModule.class,
        ApiModule.class
})
public interface ApplicationComponent {

    DataManagerComponent plus(DataManagerModule userModule);

    void inject(AppUtil appUtil);

    void inject(LoggingExceptionHandler loggingExceptionHandler);

    void inject(ExchangeRateFactory exchangeRateFactory);

    void inject(ReceiveCurrencyHelper receiveCurrencyHelper);

}