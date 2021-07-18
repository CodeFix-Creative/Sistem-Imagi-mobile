package com.imagi.app.di.module

import com.imagi.app.*
import com.imagi.app.ui.home.MapsFragment
import com.imagi.app.ui.merchent.HomeFragment
import com.imagi.app.ui.merchent.SearchActivityPage
import com.imagi.app.ui.merchent.StoreMerchant
import com.imagi.app.ui.merchent.StoreMerchantRetail
import com.imagi.app.ui.login.LoginActivity
import com.imagi.app.ui.market.ActivityProductDetail
import com.imagi.app.ui.review.ReviewActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    internal abstract fun contributeLoginActivity(): LoginActivity


    @ContributesAndroidInjector
    internal abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun contributeMenuMerchant(): MenuMerchant

    @ContributesAndroidInjector
    internal abstract fun contributeFragmentProfile(): ProfilePage

    @ContributesAndroidInjector
    internal abstract fun contributeFragmentMarket(): MarketFragment

    @ContributesAndroidInjector
    internal abstract fun contributeFragmentHome(): HomeFragment

    @ContributesAndroidInjector
    internal abstract fun contributeDetailMarketActivity(): DetailMarket

    @ContributesAndroidInjector
    internal abstract fun contributeReviewActivity(): ReviewActivity

    @ContributesAndroidInjector
    internal abstract fun contributeFeedbackActivity(): FeedbackActivity

    @ContributesAndroidInjector
    internal abstract fun contributeFragmentStoreMerchant(): StoreMerchant

    @ContributesAndroidInjector
    internal abstract fun contributeSearchActivity(): SearchActivityPage

    @ContributesAndroidInjector
    internal abstract fun contributeProductDetailActivity(): ActivityProductDetail

    @ContributesAndroidInjector
    internal abstract fun contributeStoreMerchantRetail(): StoreMerchantRetail

    @ContributesAndroidInjector
    internal abstract fun contributeMapsFragments(): MapsFragment




}