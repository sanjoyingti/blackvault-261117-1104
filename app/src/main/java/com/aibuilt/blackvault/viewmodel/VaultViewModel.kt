package com.aibuilt.blackvault.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aibuilt.blackvault.data.BankDatabase
import com.aibuilt.blackvault.data.BreachRecord
import com.aibuilt.blackvault.data.Settings
import com.aibuilt.blackvault.data.VaultRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

data class StealthState(
    val ipSpoofing: Boolean = true,
    val macRandomiser: Boolean = true,
    val dnsLeakBlocker: Boolean = true,
    val traceScrubber: Boolean = true,
    val antiForensics: Boolean = true
) {
    val allEnabled: Boolean
        get() = ipSpoofing && macRandomiser && dnsLeakBlocker && traceScrubber && antiForensics
}

class VaultViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VaultRepository(application)

    val history: StateFlow<List<BreachRecord>> = repository.history
    val settings: Flow<Settings> = repository.settings

    private val _accountNumber = MutableStateFlow("")
    val accountNumber: StateFlow<String> = _accountNumber.asStateFlow()

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code.asStateFlow()

    private val _stealth = MutableStateFlow(StealthState())
    val stealth: StateFlow<StealthState> = _stealth.asStateFlow()

    private val _ip = MutableStateFlow(randomIp())
    val ip: StateFlow<String> = _ip.asStateFlow()

    private val _traceBlocked = MutableStateFlow(0)
    val traceBlocked: StateFlow<Int> = _traceBlocked.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    private val _breachLines = MutableStateFlow<List<String>>(emptyList())
    val breachLines: StateFlow<List<String>> = _breachLines.asStateFlow()

    private val _breachProgress = MutableStateFlow(0f)
    val breachProgress: StateFlow<Float> = _breachProgress.asStateFlow()

    private val _breachRunning = MutableStateFlow(false)
    val breachRunning: StateFlow<Boolean> = _breachRunning.asStateFlow()

    private val _finishedRecordId = MutableStateFlow<String?>(null)
    val finishedRecordId: StateFlow<String?> = _finishedRecordId.asStateFlow()

    private var breachJob: Job? = null

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(3000L)
                _ip.value = randomIp()
            }
        }
    }

    fun setAccountNumber(value: String) {
        _accountNumber.value = value.filter { it.isDigit() }.take(18)
        _validationError.value = null
    }

    fun setCode(value: String) {
        _code.value = value.uppercase().filter { it.isLetterOrDigit() }.take(11)
        _validationError.value = null
    }

    fun setIpSpoofing(value: Boolean) { _stealth.value = _stealth.value.copy(ipSpoofing = value) }
    fun setMacRandomiser(value: Boolean) { _stealth.value = _stealth.value.copy(macRandomiser = value) }
    fun setDnsLeakBlocker(value: Boolean) { _stealth.value = _stealth.value.copy(dnsLeakBlocker = value) }
    fun setTraceScrubber(value: Boolean) { _stealth.value = _stealth.value.copy(traceScrubber = value) }
    fun setAntiForensics(value: Boolean) { _stealth.value = _stealth.value.copy(antiForensics = value) }

    fun clearValidationError() { _validationError.value = null }

    fun setValidationError(message: String) { _validationError.value = message }

    fun startBreach() {
        val account = _accountNumber.value.trim()
        val codeValue = _code.value.trim().uppercase()

        if (account.isEmpty()) {
            _validationError.value = "ACCOUNT NUMBER REQUIRED"
            return
        }
        if (!BankDatabase.isValidCode(codeValue)) {
            _validationError.value = "INVALID IFSC OR ROUTING NUMBER"
            return
        }
        _validationError.value = null

        val bank = BankDatabase.detect(codeValue)

        breachJob?.cancel()
        _breachLines.value = emptyList()
        _breachProgress.value = 0f
        _finishedRecordId.value = null
        _breachRunning.value = true

        breachJob = viewModelScope.launch {
            val tracker = launch {
                while (isActive) {
                    delay(900L)
                    _traceBlocked.value = _traceBlocked.value + Random.nextInt(1, 4)
                }
            }
            try {
                val steps = buildTimeline()
                val total = steps.sumOf { it.first } + TAIL_MS
                val started = SystemClock.elapsedRealtime()

                for (step in steps) {
                    delay(step.first)
                    _breachLines.value = _breachLines.value + step.second
                    val elapsed = SystemClock.elapsedRealtime() - started
                    _breachProgress.value =
                        (elapsed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                }

                delay(TAIL_MS)
                _breachProgress.value = 1f
                _traceBlocked.value = _traceBlocked.value + 7

                val record = BreachRecord(
                    id = UUID.randomUUID().toString(),
                    bankName = bank?.name ?: "UNKNOWN ENTITY",
                    country = bank?.country ?: "UNKNOWN",
                    accountNumber = account,
                    code = codeValue,
                    txId = generateTxId(),
                    timestamp = System.currentTimeMillis()
                )
                repository.addRecord(record)
                _finishedRecordId.value = record.id
            } finally {
                tracker.cancel()
                _breachRunning.value = false
            }
        }
    }

    fun abortBreach() {
        breachJob?.cancel()
        breachJob = null
        _breachRunning.value = false
        _breachLines.value = emptyList()
        _breachProgress.value = 0f
        _finishedRecordId.value = null
    }

    fun deleteRecord(id: String) {
        viewModelScope.launch { repository.deleteRecord(id) }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }

    fun setSplashEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setSplashEnabled(enabled) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setSoundEnabled(enabled) }
    }

    private fun buildTimeline(): List<Pair<Long, String>> {
        val steps = mutableListOf<Pair<Long, String>>()
        steps += 600L to "resolving target ledger..."
        steps += 1000L to "handshake with SWIFT node..."
        steps += 900L to "bypassing firewall layer 1/3..."
        steps += 800L to "bypassing firewall layer 2/3..."
        steps += 800L to "bypassing firewall layer 3/3..."
        steps += 800L to "spoofing origin IP..."
        steps += 800L to "scrubbing access logs..."
        steps += 600L to "MULTI-BANK SWEEP :: ENGAGED"
        BankDatabase.sweepOrder.forEach { bankName ->
            steps += 700L to "$bankName :: BREACHED \u2713"
        }
        steps += 700L to "sweep complete - 14 ledgers compromised"
        steps += 700L to "injecting \$10,000.00 into target account..."
        steps += 600L to "verifying deposit signature..."
        steps += 500L to "shredding forensic trail..."
        return steps
    }

    companion object {
        private const val TAIL_MS = 1200L

        fun randomIp(): String = buildString {
            append(Random.nextInt(11, 224))
            repeat(3) {
                append('.')
                append(Random.nextInt(1, 255))
            }
        }

        fun generateTxId(): String {
            val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ0123456789"
            return "BV-" + (1..10).map { chars[Random.nextInt(chars.length)] }.joinToString("")
        }
    }
}
