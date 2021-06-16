import android.content.Context
import com.imagi.app.BuildConfig
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import com.imagi.app.component.AppComponent
import com.imagi.app.component.DaggerAppComponent
import timber.log.Timber

class ImagiApp : DaggerApplication() {

    private val appComponent = DaggerAppComponent.builder()
        .application(this)
        .build()

    init {
        instance = this
    }

    companion object {
        private  var instance: ImagiApp? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return appComponent
    }


}