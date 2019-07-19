package io.github.sinadarvi.nsd_flutter.nsd

import android.net.nsd.NsdServiceInfo
import android.os.Parcel
import android.os.Parcelable

import java.net.InetAddress

/**
 * A class representing service information for network service discovery.
 */
class NsdService : Parcelable {


    /**
     * Returns service name.
     */
    val name: String?
    /**
     * Returns service type.
     */
    val type: String?
    /**
     * Returns host ip address.
     */
    val hostIp: String?
    /**
     * Returns host domain name.
     */
    val hostName: String?
    /**
     * Returns service port number.
     */
    val port: Int
    /**
     * Retruns host information.
     */
    val host: InetAddress

    constructor(nsdServiceInfo: NsdServiceInfo) {
        this.name = nsdServiceInfo.serviceName
        this.type = nsdServiceInfo.serviceType
        this.hostIp = if (nsdServiceInfo.host == null) null else nsdServiceInfo.host.hostAddress
        this.hostName = if (nsdServiceInfo.host == null) null else nsdServiceInfo.host.hostName
        this.port = nsdServiceInfo.port
        this.host = nsdServiceInfo.host
    }

    constructor(name: String, type: String, hostIp: String, hostName: String, port: Int, host: InetAddress) {
        this.name = name
        this.type = type
        this.hostIp = hostIp
        this.hostName = hostName
        this.port = port
        this.host = host
    }

    protected constructor(`in`: Parcel) {
        this.name = `in`.readString()
        this.type = `in`.readString()
        this.hostIp = `in`.readString()
        this.hostName = `in`.readString()
        this.port = `in`.readInt()
        this.host = `in`.readSerializable() as InetAddress
    }

    override fun toString(): String {
        return "name='" + name + '\''.toString() +
                ", type='" + type + '\''.toString() +
                ", hostIp='" + hostIp + '\''.toString() +
                ", hostName='" + hostName + '\''.toString() +
                ", port=" + port
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as NsdService?

        if (port != that!!.port) return false
        if (if (name != null) name != that.name else that.name != null) return false
        if (if (type != null) type != that.type else that.type != null) return false
        if (if (hostIp != null) hostIp != that.hostIp else that.hostIp != null) return false
        return if (hostName != null) hostName == that.hostName else that.hostName == null

    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (hostIp?.hashCode() ?: 0)
        result = 31 * result + (hostName?.hashCode() ?: 0)
        result = 31 * result + port
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.name)
        dest.writeString(this.type)
        dest.writeString(this.hostIp)
        dest.writeString(this.hostName)
        dest.writeInt(this.port)
        dest.writeSerializable(this.host)
    }

    companion object CREATOR : Parcelable.Creator<NsdService> {
        override fun createFromParcel(parcel: Parcel): NsdService {
            return NsdService(parcel)
        }

        override fun newArray(size: Int): Array<NsdService?> {
            return arrayOfNulls(size)
        }
    }
}
