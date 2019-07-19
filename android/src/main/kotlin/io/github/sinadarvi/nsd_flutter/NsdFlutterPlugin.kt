package io.github.sinadarvi.nsd_flutter

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.github.sinadarvi.nsd_flutter.nsd.NsdHelper
import io.github.sinadarvi.nsd_flutter.nsd.NsdListener
import io.github.sinadarvi.nsd_flutter.nsd.NsdService
import io.github.sinadarvi.nsd_flutter.nsd.NsdType


class NsdFlutterPlugin(private var registrar: Registrar) : MethodCallHandler , NsdListener{

  private lateinit var nsdHelper: NsdHelper
  private var isRegistered = false

  override fun onNsdRegistered(registeredService: NsdService) {
    isRegistered = true
  }

  override fun onNsdUnregisterd() {
    isRegistered = false
  }

  override fun onNsdDiscoveryFinished() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onNsdServiceFound(foundService: NsdService) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onNsdServiceResolved(resolvedService: NsdService) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onNsdServiceLost(lostService: NsdService) {
    isRegistered = false
  }

  override fun onNsdError(errorMessage: String, errorCode: Int, errorSource: String) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  fun isItRegistered(): Boolean {
    return isRegistered
  }

  fun initializeNsdHelper() {
    nsdHelper = NsdHelper(registrar.context(), this)
    nsdHelper.isLogEnabled = true
    nsdHelper.isAutoResolveEnabled = true
    nsdHelper.setDiscoveryTimeout(30)
  }

  fun registerIt() {
    nsdHelper.registerService("NSD_Flutter", NsdType.HTTP)
  }

  fun unregisterIt() {
    nsdHelper.unregisterService()
  }

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "nsd_flutter")
      channel.setMethodCallHandler(NsdFlutterPlugin(registrar))
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    }else if(call.method == "isItRegistered") {
      val reg = isItRegistered()
      result.success(reg.toString())
    }else if (call.method == "initializeNsdHelper"){
      initializeNsdHelper()
      result.success("true")
    }else if (call.method == "registerIt"){
      registerIt()
      result.success("true")
    }else if (call.method == "unregisterIt"){
      unregisterIt()
      result.success("true")
    } else {
      result.notImplemented()
    }
  }
}
