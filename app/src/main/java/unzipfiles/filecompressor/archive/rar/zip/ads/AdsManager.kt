package unzipfiles.filecompressor.archive.rar.zip.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.vapp.admoblibrary.AdsInterCallBack
import com.vapp.admoblibrary.ads.AdCallBackInterLoad
import com.vapp.admoblibrary.ads.AdmodUtils
import com.vapp.admoblibrary.ads.AppOpenManager
import com.vapp.admoblibrary.ads.NativeAdCallback
import com.vapp.admoblibrary.ads.admobnative.enumclass.GoogleENative
import com.vapp.admoblibrary.ads.model.InterHolder
import com.vapp.admoblibrary.ads.model.NativeHolder
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.utils.RemoteConfig
import unzipfiles.filecompressor.archive.rar.zip.utils.logEvents

object AdsManager {

    var interTheme = InterHolder("")
    var interIntro = InterHolder("")
    var interLanguage = InterHolder("")

//    var interArchiver = InterHolder("")
//    var interImage= InterHolder("")
//    var interVideo = InterHolder("")
//    var interAudio = InterHolder("")
//    var interDocuments = InterHolder("")
//    var interDowload = InterHolder("")

    var interHome = InterHolder("ca-app-pub-8475252859305547/4730944661")

    var interProcess= InterHolder("ca-app-pub-8475252859305547/2619824639")
    var interDirectory = InterHolder("ca-app-pub-8475252859305547/8224053846")

    var nativeHome = NativeHolder("ca-app-pub-8475252859305547/6440198880")
    var nativeProcess = NativeHolder("ca-app-pub-8475252859305547/2556207464")
    var nativeLanguage = NativeHolder("")
    var nativeId = NativeHolder("ca-app-pub-8475252859305547/7913062657")
    var nativeDirectory = NativeHolder("ca-app-pub-8475252859305547/3973817641")

    var nativeIntro = NativeHolder("")

    var appID = ""
    var onResumeID = "ca-app-pub-8475252859305547/6757585044"

    // ------------------------ BASE -----------------------
    var base_interTheme = InterHolder("")
    var base_interIntro = InterHolder("")

    var base_nativeLanguage = NativeHolder("")
    var base_nativeIntro = NativeHolder("")

    // ------------------------ A -----------------------
    var a_interLanguage = InterHolder("")
    var a_interIntro = InterHolder("")

    var a_nativeLanguage = NativeHolder("")
    var a_nativeIntro = NativeHolder("")


    fun loadInter(context: Context, interHolder: InterHolder) {
        AdmodUtils.loadAndGetAdInterstitial(context, interHolder, object :
            AdCallBackInterLoad {
            override fun onAdClosed() {

            }

            override fun onEventClickAdClosed() {

            }

            override fun onAdShowed() {

            }

            override fun onAdLoaded(interstitialAd: InterstitialAd?, isLoading: Boolean) {
                interHolder.inter = interstitialAd
                interHolder.check = isLoading
            }

            override fun onAdFail(message: String?) {
            }

        })
    }

    fun showAdInter(
        context: Context,
        interHolder: InterHolder,
        callback: AdListenerNew,
        event: String
    ) {
        context.logEvents(event + "_load")
        AppOpenManager.getInstance().isAppResumeEnabled = true
        (context as Activity?)?.let {
            AdmodUtils.showAdInterstitialWithCallbackNotLoadNew(it, interHolder,12000, object :
                AdsInterCallBack {
                override fun onStartAction() {
                    callback.onAdClosed()

                }

                override fun onEventClickAdClosed() {
                    context.logEvents(event + "_close")
                    interHolder.inter = null
                    loadInter(context, interHolder)
                }

                override fun onAdShowed() {
                    context.logEvents(event + "_showed")
                    AppOpenManager.getInstance().isAppResumeEnabled = false
                }

                override fun onAdLoaded() {

                }

                override fun onAdFail(error: String?) {
                    context.logEvents(event + "_fail")
                    callback.onFailed()
                }

                override fun onPaid(adValue: AdValue?, adUnitAds: String?) {

                }

            }, true)
        }

    }



    fun loadNative(activity: Context, nativeHolder: NativeHolder) {
        AdmodUtils.loadAndGetNativeAds(activity, nativeHolder, object : NativeAdCallback {
                override fun onLoadedAndGetNativeAd(ad: NativeAd?) {

                }

                override fun onNativeAdLoaded() {

                }

                override fun onAdFail(error: String?) {

                }

                override fun onAdPaid(adValue: AdValue?, adUnitAds: String?) {

                }

            })
    }

    fun showNative(activity: Activity, nativeHolder: NativeHolder, nativeAdContainer: ViewGroup) {
        if (!AdmodUtils.isNetworkConnected(activity)) {
            nativeAdContainer.visibility = View.GONE
            return
        }
       showAdNativeWithSize(activity, nativeAdContainer, nativeHolder, GoogleENative.UNIFIED_MEDIUM, R.layout.ad_unified_medium)
    }

    fun showNativeTopButton(activity: Activity, nativeHolder: NativeHolder, nativeAdContainer: ViewGroup) {
        if (!AdmodUtils.isNetworkConnected(activity)) {
            nativeAdContainer.visibility = View.GONE
            return
        }
        showAdNativeWithSize(activity, nativeAdContainer, nativeHolder, GoogleENative.UNIFIED_MEDIUM, R.layout.ad_unified_medium_top_button)
    }



    private fun showAdNativeWithSize(
        activity: Activity,
        nativeAdContainer: ViewGroup,
        nativeHolder: NativeHolder,
        googleENative: GoogleENative,
        layout: Int
    ) {
        if (!AdmodUtils
                .isNetworkConnected(activity) || nativeHolder.nativeAd == null
        ) {
            nativeAdContainer.visibility = View.GONE
            return
        }
        AdmodUtils.showNativeAdsWithLayout(
            activity,
            nativeHolder,
            nativeAdContainer,
            layout,
            googleENative, object : AdmodUtils.AdsNativeCallBackAdmod {
                override fun NativeLoaded() {
                    nativeAdContainer.visibility = View.VISIBLE
                }

                override fun onPaidNative(adValue: AdValue, adUnitAds: String) {

                }

                override fun NativeFailed(massage: String) {
                    nativeAdContainer.visibility = View.GONE
                }

            }
        )
    }

    fun showAdBanner(activity: Activity, adsEnum: String, view: ViewGroup, line: View) {
        if (AdmodUtils.isNetworkConnected(activity)) {
            AdmodUtils.loadAdBanner(activity, adsEnum, view, object :
                AdmodUtils.BannerCallBack {
                override fun onLoad() {
                    view.visibility = View.VISIBLE
                    line.visibility = View.VISIBLE
                }

                override fun onFailed(message: String) {
                    view.visibility = View.GONE
                    line.visibility = View.GONE
                }

                override fun onPaid(adValue: AdValue?, mAdView: AdView?) {

                }
            })
        } else {
            view.visibility = View.GONE
            line.visibility = View.GONE
        }
    }

    interface AdListenerNew {
        fun onAdClosed()
        fun onFailed()
    }

    fun checkABIdAds() {
        when (RemoteConfig.version) {
            "loading_theme" -> {
                interTheme = InterHolder("ca-app-pub-8475252859305547/8146649334")
                interIntro = InterHolder("ca-app-pub-8475252859305547/2627525503")

                nativeLanguage = NativeHolder("ca-app-pub-8475252859305547/9995466367")
                nativeIntro = NativeHolder("ca-app-pub-8475252859305547/6080272080")
                Log.d("===Id", "Baseline")
            }
            else -> {
                interLanguage = InterHolder("ca-app-pub-8475252859305547/9268159310")
                interIntro = InterHolder("ca-app-pub-8475252859305547/2702750965")

                nativeLanguage = NativeHolder("ca-app-pub-8475252859305547/7305137115")
                nativeIntro = NativeHolder("ca-app-pub-8475252859305547/1949455385")
                Log.d("===Id", "B")
            }
        }
    }
}