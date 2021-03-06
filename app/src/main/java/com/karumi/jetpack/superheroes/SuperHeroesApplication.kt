package com.karumi.jetpack.superheroes

import android.app.Application
import android.content.Context
import com.github.salomonbrys.kodein.Kodein.Module
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.conf.ConfigurableKodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton
import com.karumi.jetpack.superheroes.data.repository.LocalSuperHeroDataSource
import com.karumi.jetpack.superheroes.data.repository.RemoteSuperHeroDataSource
import com.karumi.jetpack.superheroes.data.repository.SuperHeroRepository

class SuperHeroesApplication : Application(), KodeinAware {
    override val kodein = ConfigurableKodein(mutable = true)
    var overrideModule: Module? = null

    override fun onCreate() {
        super.onCreate()
        resetInjection()
    }

    fun addModule(activityModules: Module) {
        kodein.addImport(activityModules, true)
        if (overrideModule != null) {
            kodein.addImport(overrideModule!!, true)
        }
    }

    fun resetInjection() {
        kodein.clear()
        kodein.addImport(appDependencies(), true)
    }

    private fun appDependencies(): Module {
        return Module(allowSilentOverride = true) {
            bind<SuperHeroRepository>() with provider {
                SuperHeroRepository(instance(), instance())
            }
            bind<LocalSuperHeroDataSource>() with singleton {
                LocalSuperHeroDataSource()
            }
            bind<RemoteSuperHeroDataSource>() with provider {
                RemoteSuperHeroDataSource()
            }
        }
    }
}

fun Context.asApp() = this.applicationContext as SuperHeroesApplication