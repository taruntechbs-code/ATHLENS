package com.athlens.app.data.p2p

import android.content.Context
import android.util.Log
import com.athlens.app.data.local.entity.ReceivedPassportEntity
import com.athlens.app.data.security.PassportCryptoManager
import com.athlens.app.domain.passport.PassportSerializer
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.security.MessageDigest
import java.util.UUID

enum class P2PExchangeState {
    IDLE,
    ADVERTISING,
    DISCOVERING,
    CONNECTING,
    CONNECTED,
    TRANSFER_SUCCESS,
    ERROR
}

data class DiscoveredEndpoint(
    val id: String,
    val name: String
)

class NearbyExchangeManager(
    private val context: Context,
    private val passportCryptoManager: PassportCryptoManager
) {
    companion object {
        private const val TAG = "NearbyExchangeManager"
        const val SERVICE_ID = "com.athlens.app.P2P_EXCHANGE"
    }

    private val client: ConnectionsClient = Nearby.getConnectionsClient(context.applicationContext)

    private val _state = MutableStateFlow(P2PExchangeState.IDLE)
    val state: StateFlow<P2PExchangeState> = _state.asStateFlow()

    private val _discoveredEndpoints = MutableStateFlow<List<DiscoveredEndpoint>>(emptyList())
    val discoveredEndpoints: StateFlow<List<DiscoveredEndpoint>> = _discoveredEndpoints.asStateFlow()

    private val _statusMessage = MutableStateFlow("Ready for offline P2P transfer")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private var pendingPayloadToSend: String? = null
    private var onPassportReceivedCallback: ((ReceivedPassportEntity) -> Unit)? = null
    private var connectedEndpointId: String? = null

    // SENDER FLOW: Advertise and send signed talent passport
    fun startSharingPassport(senderName: String, signedEnvelopeJson: String) {
        stopAll()
        pendingPayloadToSend = signedEnvelopeJson
        _state.value = P2PExchangeState.ADVERTISING
        _statusMessage.value = "Broadcasting passport via Nearby P2P..."

        val advertisingOptions = AdvertisingOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()

        client.startAdvertising(
            senderName,
            SERVICE_ID,
            senderConnectionLifecycleCallback,
            advertisingOptions
        ).addOnSuccessListener {
            Log.i(TAG, "Nearby advertising started for $senderName")
            _statusMessage.value = "Broadcasting: Waiting for coach/scout to connect..."
        }.addOnFailureListener { e ->
            Log.e(TAG, "Advertising failed: ${e.message}", e)
            _state.value = P2PExchangeState.ERROR
            _statusMessage.value = "Failed to start advertising: ${e.localizedMessage}"
        }
    }

    // RECEIVER FLOW: Discover nearby athletes and receive signed passport
    fun startDiscovering(scoutName: String, onPassportReceived: (ReceivedPassportEntity) -> Unit) {
        stopAll()
        onPassportReceivedCallback = onPassportReceived
        _discoveredEndpoints.value = emptyList()
        _state.value = P2PExchangeState.DISCOVERING
        _statusMessage.value = "Scanning for nearby athletes sharing passports..."

        val discoveryOptions = DiscoveryOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()

        client.startDiscovery(
            SERVICE_ID,
            object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                    Log.i(TAG, "Discovered endpoint: $endpointId (${info.endpointName})")
                    _discoveredEndpoints.update { current ->
                        if (current.none { it.id == endpointId }) {
                            current + DiscoveredEndpoint(endpointId, info.endpointName)
                        } else current
                    }
                }

                override fun onEndpointLost(endpointId: String) {
                    Log.i(TAG, "Lost endpoint: $endpointId")
                    _discoveredEndpoints.update { current -> current.filter { it.id != endpointId } }
                }
            },
            discoveryOptions
        ).addOnSuccessListener {
            Log.i(TAG, "Nearby discovery started for $scoutName")
        }.addOnFailureListener { e ->
            Log.e(TAG, "Discovery failed: ${e.message}", e)
            _state.value = P2PExchangeState.ERROR
            _statusMessage.value = "Failed to start discovery: ${e.localizedMessage}"
        }
    }

    // Connect to a discovered athlete endpoint
    fun connectToEndpoint(endpointId: String, scoutName: String) {
        _state.value = P2PExchangeState.CONNECTING
        _statusMessage.value = "Connecting to athlete..."

        client.requestConnection(
            scoutName,
            endpointId,
            receiverConnectionLifecycleCallback
        ).addOnFailureListener { e ->
            Log.e(TAG, "Connection request failed: ${e.message}", e)
            _state.value = P2PExchangeState.ERROR
            _statusMessage.value = "Connection failed: ${e.localizedMessage}"
        }
    }

    private val senderConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.i(TAG, "Incoming connection from ${connectionInfo.endpointName} ($endpointId) - auto accepting")
            client.acceptConnection(endpointId, senderPayloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                Log.i(TAG, "Connected to scout $endpointId. Sending signed passport payload...")
                connectedEndpointId = endpointId
                _state.value = P2PExchangeState.CONNECTED
                _statusMessage.value = "Connected! Transferring signed passport..."

                pendingPayloadToSend?.let { json ->
                    val bytes = json.toByteArray(Charsets.UTF_8)
                    client.sendPayload(endpointId, Payload.fromBytes(bytes))
                }
            } else {
                Log.w(TAG, "Connection to $endpointId failed: ${result.status.statusCode}")
                _state.value = P2PExchangeState.ERROR
                _statusMessage.value = "Connection rejected or failed."
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.i(TAG, "Disconnected from $endpointId")
            if (_state.value != P2PExchangeState.TRANSFER_SUCCESS) {
                _state.value = P2PExchangeState.IDLE
                _statusMessage.value = "Disconnected."
            }
        }
    }

    private val senderPayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            // Sender doesn't expect data back
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (update.status == PayloadTransferUpdate.Status.SUCCESS) {
                Log.i(TAG, "Payload successfully transferred to $endpointId")
                _state.value = P2PExchangeState.TRANSFER_SUCCESS
                _statusMessage.value = "Passport successfully transferred to scout!"
            } else if (update.status == PayloadTransferUpdate.Status.FAILURE) {
                Log.e(TAG, "Payload transfer failed to $endpointId")
                _state.value = P2PExchangeState.ERROR
                _statusMessage.value = "Transfer failed."
            }
        }
    }

    private val receiverConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.i(TAG, "Connection initiated with athlete ${connectionInfo.endpointName} - accepting")
            client.acceptConnection(endpointId, receiverPayloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                Log.i(TAG, "Connected to athlete $endpointId. Awaiting payload...")
                connectedEndpointId = endpointId
                _state.value = P2PExchangeState.CONNECTED
                _statusMessage.value = "Connected! Awaiting passport transmission..."
            } else {
                Log.w(TAG, "Connection failed: ${result.status.statusCode}")
                _state.value = P2PExchangeState.ERROR
                _statusMessage.value = "Connection failed."
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.i(TAG, "Disconnected from athlete $endpointId")
            if (_state.value != P2PExchangeState.TRANSFER_SUCCESS) {
                _state.value = P2PExchangeState.IDLE
            }
        }
    }

    private val receiverPayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                val bytes = payload.asBytes() ?: return
                val envelopeJson = String(bytes, Charsets.UTF_8)
                Log.i(TAG, "Received payload envelope from $endpointId: ${envelopeJson.take(80)}...")

                val envelope = PassportSerializer.parseEnvelope(envelopeJson)
                if (envelope != null) {
                    val (canonicalJson, signatureBase64, pubKeyBase64) = envelope
                    val parsedPayload = PassportSerializer.parseCanonicalPayload(canonicalJson)

                    // 100% Offline Cryptographic Verification
                    val isValid = passportCryptoManager.verifySignature(
                        canonicalJson,
                        signatureBase64,
                        pubKeyBase64
                    )

                    // Compute public key fingerprint
                    val fingerprint = try {
                        val keyBytes = android.util.Base64.decode(pubKeyBase64, android.util.Base64.NO_WRAP)
                        val md = MessageDigest.getInstance("SHA-256")
                        val hash = md.digest(keyBytes)
                        hash.take(6).joinToString("") { "%02X".format(it) }
                    } catch (e: Exception) {
                        "P2P-KEY"
                    }

                    val entity = ReceivedPassportEntity(
                        id = UUID.randomUUID().toString(),
                        athleteName = parsedPayload?.fullName ?: "Verified Athlete",
                        district = parsedPayload?.district ?: "Coimbatore",
                        sport = parsedPayload?.sport ?: "KABADDI",
                        playingPosition = parsedPayload?.playingPosition ?: "Raider",
                        ageMonths = parsedPayload?.ageMonths ?: 0,
                        raeQuarter = parsedPayload?.raeQuarter ?: "Q1",
                        totalSessions = parsedPayload?.totalSessions ?: 0,
                        avgFormScore = parsedPayload?.avgScore ?: 0,
                        verifiedAchievementsCount = parsedPayload?.verifiedAchievements ?: 0,
                        signatureVerified = isValid,
                        publicKeyFingerprint = fingerprint,
                        transferMethod = "NEARBY_P2P",
                        rawEnvelopeJson = envelopeJson,
                        receivedAt = System.currentTimeMillis(),
                        synced = false
                    )

                    onPassportReceivedCallback?.invoke(entity)
                    _state.value = P2PExchangeState.TRANSFER_SUCCESS
                    _statusMessage.value = if (isValid) {
                        "Received & Authenticated: ${entity.athleteName} (ECDSA P-256 Verified)"
                    } else {
                        "Received: ${entity.athleteName} (Warning: Signature Mismatch)"
                    }
                } else {
                    _state.value = P2PExchangeState.ERROR
                    _statusMessage.value = "Received corrupted or unreadable passport payload."
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Handled when payload bytes arrive
        }
    }

    fun stopAll() {
        try {
            client.stopAdvertising()
            client.stopDiscovery()
            client.stopAllEndpoints()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping Nearby operations: ${e.message}")
        }
        connectedEndpointId = null
        pendingPayloadToSend = null
        _state.value = P2PExchangeState.IDLE
        _discoveredEndpoints.value = emptyList()
        _statusMessage.value = "Ready for offline P2P transfer"
    }
}
