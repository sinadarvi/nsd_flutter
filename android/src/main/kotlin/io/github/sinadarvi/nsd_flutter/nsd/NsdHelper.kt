package io.github.sinadarvi.nsd_flutter.nsd

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo

import java.io.IOException
import java.net.ServerSocket

import android.util.Log.d
import android.util.Log.e

/**
 * NsdHelper is a helper class for [NsdManager].
 */
class NsdHelper : DiscoveryTimer.OnTimeoutListener {
    private val mNsdManager: NsdManager
    var nsdListener: NsdListener? = null

    // Registration
    private var mRegistered = false
    private var mRegistrationListener: NsdListenerRegistration? = null
    var registeredService: NsdService? = null
        private set
    internal var registeredServiceInfo = NsdServiceInfo()
        private set


    // Discovery
    /**
     * @return True if discovery is running.
     */
    var isDiscoveryRunning = false
        private set
    /**
     * @return onNsdDiscoveryTimeout timeout in seconds.
     */
    var discoveryTimeout: Long = 15
        private set
    /**
     * @return Discovery service type to discover.
     */
    var discoveryServiceType: String? = null
        private set
    /**
     * @return Discovery service name to discover.
     */
    var discoveryServiceName: String? = null
        private set
    private var mDiscoveryListener: NsdListenerDiscovery? = null
    private var mDiscoveryTimer: DiscoveryTimer? = null

    // Resolve
    /**
     * @return Is auto resolving discovered services enabled.
     */
    /**
     * Enable auto resolving discovered services.
     * By default it's enabled.
     *
     * @param isAutoResolveEnabled If true discovered service will be automatically resolved.
     */
    var isAutoResolveEnabled = true
    private var mResolveQueue: ResolveQueue? = null

    // Common
    /**
     * @return Is logcat enabled.
     */
    /**
     * Enable logcat messages.
     * By default their disabled.
     *
     * @param isLogEnabled If true logcat is enabled.
     */
    var isLogEnabled = false

    /**
     * @param nsdManager Android [NsdManager].
     */
    constructor(nsdManager: NsdManager) {
        this.mNsdManager = nsdManager
        this.mDiscoveryTimer = DiscoveryTimer(this, discoveryTimeout)
        this.mResolveQueue = ResolveQueue(this)
    }

    /**
     * @param nsdManager  Android [NsdManager].
     * @param nsdListener Service discovery listener.
     */
    constructor(nsdManager: NsdManager, nsdListener: NsdListener) {
        this.mNsdManager = nsdManager
        this.nsdListener = nsdListener
        this.mDiscoveryTimer = DiscoveryTimer(this, discoveryTimeout)
        this.mResolveQueue = ResolveQueue(this)
    }

    /**
     * @param context Context is only needed to create [NsdManager] instance.
     */
    constructor(context: Context) {
        this.mNsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        this.mDiscoveryTimer = DiscoveryTimer(this, discoveryTimeout)
        this.mResolveQueue = ResolveQueue(this)
    }

    /**
     * @param context     Context is only needed to create [NsdManager] instance.
     * @param nsdListener Service discovery listener.
     */
    constructor(context: Context, nsdListener: NsdListener) {
        this.mNsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        this.nsdListener = nsdListener
        this.mDiscoveryTimer = DiscoveryTimer(this, discoveryTimeout)
        this.mResolveQueue = ResolveQueue(this)
    }

    /**
     * If no new service has been discovered for a timeout interval, discovering will be stopped
     * and [NsdListener.onNsdDiscoveryFinished] will be called.
     * Default timeout is set to 15 seconds.
     * Set 0 to infinite.
     */
    fun setDiscoveryTimeout(seconds: Int) {
        if (seconds < 0)
            throw IllegalArgumentException("Timeout has to be greater or equal 0!")

        if (seconds == 0)
            discoveryTimeout = Integer.MAX_VALUE.toLong()
        else
            discoveryTimeout = seconds.toLong()

        mDiscoveryTimer!!.timeout(discoveryTimeout)
    }

    /**
     * Register new service with given serivce name, type and port.
     *
     * @param desiredServiceName Desired service name. If the name already exists in network it will be change to something like 'AppChat (1)'.
     * @param serviceType        Service type.
     * @param port               Service port.
     */
    @JvmOverloads
    fun registerService(desiredServiceName: String, serviceType: String, port: Int = findAvaiablePort()) {
        if (port == 0) return

        registeredServiceInfo = NsdServiceInfo()
        registeredServiceInfo.serviceName = desiredServiceName
        registeredServiceInfo.serviceType = serviceType
        registeredServiceInfo.port = port

        mRegistrationListener = NsdListenerRegistration(this)
        mNsdManager.registerService(registeredServiceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener)
    }

    fun unregisterService() {
        if (mRegistered) {
            mRegistered = false
            mNsdManager.unregisterService(mRegistrationListener)
        }
    }

    /**
     * Start discovering services.
     *
     * @param serviceType Service type, eg. "_http._tcp.". Typical names can be obtain from [NsdType]
     * @param serviceName When service is discovered it will check if it's name contains this text. It is NOT case sensitive.
     */
    @JvmOverloads
    fun startDiscovery(serviceType: String, serviceName: String? = null) {
        if (!isDiscoveryRunning) {
            isDiscoveryRunning = true
            mDiscoveryTimer!!.start()
            discoveryServiceType = serviceType
            discoveryServiceName = serviceName
            mDiscoveryListener = NsdListenerDiscovery(this)
            mNsdManager.discoverServices(discoveryServiceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener)
        }
    }

    /**
     * Stop discovering services.
     */
    fun stopDiscovery() {
        if (isDiscoveryRunning) {
            isDiscoveryRunning = false
            mDiscoveryTimer!!.cancel()
            mNsdManager.stopServiceDiscovery(mDiscoveryListener)
            if (nsdListener != null)
                nsdListener!!.onNsdDiscoveryFinished()
        }
    }

    /**
     * Resolve service host and port.
     *
     * @param nsdService Service to be resolved.
     */
    fun resolveService(nsdService: NsdService) {
        val serviceInfo = NsdServiceInfo()
        serviceInfo.serviceName = nsdService.name
        serviceInfo.serviceType = nsdService.type
        mResolveQueue!!.enqueue(serviceInfo)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Internals
    ///////////////////////////////////////////////////////////////////////////

    internal fun resolveService(serviceInfo: NsdServiceInfo) {
        mNsdManager.resolveService(serviceInfo, NsdListenerResolve(this))
    }

    internal fun findAvaiablePort(): Int {
        val socket: ServerSocket
        try {
            socket = ServerSocket(0)
            return socket.localPort
        } catch (e: IOException) {
            logError("Couldn't assign port to your service.", 0, "java.net.ServerSocket")
            e.printStackTrace()
            return 0
        }

    }

    internal fun onRegistered(serviceName: String) {
        mRegistered = true
        registeredServiceInfo.serviceName = serviceName
        registeredService = NsdService(registeredServiceInfo)
        if (nsdListener != null)
            nsdListener!!.onNsdRegistered(registeredService!!)
    }

    internal fun onNsdServiceFound(foundService: NsdServiceInfo) {
        mDiscoveryTimer!!.reset()
        if (nsdListener != null)
            nsdListener!!.onNsdServiceFound(NsdService(foundService))
        if (isAutoResolveEnabled)
            mResolveQueue!!.enqueue(foundService)
    }

    internal fun onNsdServiceResolved(resolvedService: NsdServiceInfo) {
        mResolveQueue!!.next()
        mDiscoveryTimer!!.reset()
        if (nsdListener != null)
            nsdListener!!.onNsdServiceResolved(NsdService(resolvedService))
    }

    internal fun onNsdServiceLost(lostService: NsdServiceInfo) {
        if (nsdListener != null)
            nsdListener!!.onNsdServiceLost(NsdService(lostService))
    }

    internal fun logMsg(msg: String) {
        if (isLogEnabled)
            d(TAG, msg)
    }

    internal fun logError(errorMessage: String, errorCode: Int, errorSource: String) {
        e(TAG, errorMessage)
        if (nsdListener != null)
            nsdListener!!.onNsdError(errorMessage, errorCode, errorSource)
    }

    override fun onNsdDiscoveryTimeout() {
        stopDiscovery()
    }

    companion object {
        private val TAG = "NsdHelper"
    }


}

