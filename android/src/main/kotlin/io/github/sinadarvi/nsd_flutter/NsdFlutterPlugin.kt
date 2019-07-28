package io.github.sinadarvi.nsd_flutter

import android.util.Log
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.github.sinadarvi.nsd_flutter.nsd.NsdHelper
import io.github.sinadarvi.nsd_flutter.nsd.NsdListener
import io.github.sinadarvi.nsd_flutter.nsd.NsdService
import io.github.sinadarvi.nsd_flutter.nsd.NsdType
import android.R.attr.host


class NsdFlutterPlugin(private var registrar: Registrar) : MethodCallHandler, NsdListener, EventChannel.StreamHandler {


    private lateinit var nsdHelper: NsdHelper
    //  private var registered = false
    private var eventSink: EventChannel.EventSink? = null

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val plugin = NsdFlutterPlugin(registrar)
            val channel = MethodChannel(registrar.messenger(), "nsdMethodChannel")
            channel.setMethodCallHandler(plugin)

            val registeredEventChannel = EventChannel(registrar.messenger(), "registeredEventChannel")
            registeredEventChannel.setStreamHandler(plugin)
        }
    }

    override fun onNsdRegistered(registeredService: NsdService) {
        registrar.activity().runOnUiThread {
            eventSink?.success(true)
        }
    }

    override fun onNsdUnregisterd() {
        registrar.activity().runOnUiThread {
            eventSink?.success(false)
        }
    }

    override fun onNsdDiscoveryFinished() {
        Log.e("NsdFlutter", "onNsdDiscoveryFinished")
    }

    override fun onNsdServiceFound(foundService: NsdService) {
        Log.e("NsdFlutter", "onNsdServiceFound, hostIp: '${foundService.hostIp}', host: '${foundService.host}', hostName: '${foundService.hostName}'")
    }

    override fun onNsdServiceResolved(resolvedService: NsdService) {
        Log.e("NsdFlutter", "onNsdServiceResolved, hostIp: '${resolvedService.hostIp}', host: '${resolvedService.host}', hostName: '${resolvedService.hostName}'")
    }

    override fun onNsdServiceLost(lostService: NsdService) {
        Log.e("NsdFlutter", "onNsdServiceLost")
    }

    override fun onNsdError(errorMessage: String, errorCode: Int, errorSource: String) {
        Log.e("NsdFlutter", "onNsdError, errorMessage: '$errorMessage', errorCode: $errorCode, errorSource: $errorSource")
    }

    private fun initializeNsdHelper(isLogEnabled: Boolean, isAutoResolveEnabled: Boolean, discoveryTimeout: Int) {
        //TODO modifying this is next step
        nsdHelper = NsdHelper(registrar.context(), this)
        nsdHelper.isLogEnabled = isLogEnabled
        nsdHelper.isAutoResolveEnabled = isAutoResolveEnabled
        nsdHelper.setDiscoveryTimeout(discoveryTimeout)
        Log.e("NsdFlutter", "isLogEnabled: $isLogEnabled isAutoResolveEnabled: $isAutoResolveEnabled discoveryTimeout: $discoveryTimeout")
    }

    private fun register(desiredServiceName: String, nsdType: String) {
        if (nsdType != NsdType.HTTP && nsdType != NsdType.PRINTER)
            throw Exception("nsdType is not one of NSD Types, nsdType : $nsdType")
        nsdHelper.registerService(desiredServiceName, nsdType)
        Log.e("NsdFlutter", "desiredServiceName: $desiredServiceName nsdType: $nsdType")
    }

    private fun unregister() {
        nsdHelper.unregisterService()
    }

    override fun onListen(args: Any?, eventSink: EventChannel.EventSink?) {
        this.eventSink = eventSink
    }

    override fun onCancel(args: Any?) {
        this.eventSink = null
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "initializeNsdHelper" -> {
                val isLogEnabled = call.argument<Boolean>("isLogEnabled")
                val isAutoResolveEnabled = call.argument<Boolean>("isAutoResolveEnabled")
                val discoveryTimeout = call.argument<Int>("discoveryTimeout")
                try {
                    when {
                        isLogEnabled == null -> throw Exception("isLogEnabled is null")
                        isAutoResolveEnabled == null -> throw Exception("isAutoResolveEnabled is null")
                        discoveryTimeout == null -> throw Exception("isAutoResolveEnabled is null")
                        else -> {
                            initializeNsdHelper(isLogEnabled, isAutoResolveEnabled, discoveryTimeout)
                            result.success(null)
                        }
                    }

                } catch (e: Exception) {
                    result.error("InitializeError", e.message, null)
                }
            }
            "register" -> {
                val desiredServiceName = call.argument<String>("desiredServiceName")
                val type = call.argument<String>("type")
                try {
                    if (desiredServiceName == null) {
                        //TODO : Create suitable exception for this situation.
                        throw Exception("Register Desired Service Name is null")
                    } else if (type == null) {
                        throw Exception("Register Type is null")
                    }
                    register(desiredServiceName, type)
                    result.success(null)
                } catch (e: Exception) {
                    result.error("registerError", e.message, null)
                }
            }
            "unregister" -> {
                unregister()
                result.success(null)
            }
            "startDiscovery" -> {
                val nsdType = call.argument<String>("nsdType")
                try {
                    if (nsdType == null) {
                        throw Exception("nsdType is null")
                    } else if (nsdType != NsdType.HTTP && nsdType != NsdType.PRINTER)
                        throw Exception("nsdType is not one of NSD Types, nsdType : $nsdType")
                    nsdHelper.startDiscovery(nsdType)
                    result.success(null)
                } catch (e: Exception) {
                    result.error("startDiscoveryError", e.message, e)
                }
                nsdHelper.startDiscovery(NsdType.HTTP)
                result.success(null)
            }
            "stopDiscovery" -> {
                nsdHelper.stopDiscovery()
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }
}
