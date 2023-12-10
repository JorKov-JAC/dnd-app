package makovacs.dnd

import android.app.Application
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

/**
 * Main coding: Jordan
 * This file allows us to provide a single ("static") module that can be accessed
 * everywhere in the code, and in turn provide the specific (singleton) objects we will inject.
 */
class MyApp : Application() {

    /* Always be able to access the module ("static") */
    companion object {
        lateinit var appModule: AppModule
    }

    /* Called only once at beginning of application's lifetime */
    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(this, Firebase.auth, FirebaseFirestore.getInstance())
    }
}
