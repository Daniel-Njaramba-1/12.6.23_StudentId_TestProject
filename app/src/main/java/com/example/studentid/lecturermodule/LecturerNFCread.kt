package com.example.studentid.lecturermodule

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
//import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
//import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.studentid.R
import java.nio.charset.Charset
import java.util.Arrays
import android.widget.TextView
import android.util.Log
//import androidx.compose.material3.Text
import java.io.UnsupportedEncodingException
import java.lang.Exception

private const val TAG = "NFCread"

class LecturerNFCread : AppCompatActivity() {
    private var intentFiltersArray: Array<IntentFilter>? = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED))
    private val techListsArray = arrayOf(arrayOf(NfcA::class.java.name))
    private val nfcAdapter: NfcAdapter? by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }
    private var pendingIntent: PendingIntent? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.lecturer_nfc_read)
        Log.d(TAG, "OnCreate called")

        try {
            val btnwrite = findViewById<Button>(R.id.btnwrite)
            btnwrite.setOnClickListener {
                val intent = Intent(this, NFCwrite::class.java)
                startActivity(intent)
            }
            //nfc process start
            pendingIntent = PendingIntent.getActivity(
                this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE
            )
            val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
            try {
                ndef.addDataType("text/plain")
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("fail", e)
            }
            intentFiltersArray = arrayOf(ndef)
            if (nfcAdapter == null) {
                val builder = AlertDialog.Builder(this@LecturerNFCread, R.style.MyAlertDialogStyle)
                builder.setMessage("This device doesn't support NFC.")
                builder.setPositiveButton("Cancel", null)
                val myDialog = builder.create()
                myDialog.setCanceledOnTouchOutside(false)
                myDialog.show()

            } else if (!nfcAdapter!!.isEnabled) {
                val builder = AlertDialog.Builder(this@LecturerNFCread, R.style.MyAlertDialogStyle)
                builder.setTitle("NFC Disabled")
                builder.setMessage("Please Enable NFC")

                builder.setPositiveButton("Settings") { _, _ -> startActivity(Intent(Settings.ACTION_NFC_SETTINGS)) }
                builder.setNegativeButton("Cancel", null)
                val myDialog = builder.create()
                myDialog.setCanceledOnTouchOutside(false)
                myDialog.show()
            }
        }
        catch (ex:Exception)
        {
            Toast.makeText(applicationContext, ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
    }



    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        Log.d(TAG, "onNewIntent called")
        if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            val ndefMessages = intent.parcelableArray<NdefMessage>(NfcAdapter.EXTRA_NDEF_MESSAGES)

            if (!ndefMessages.isNullOrEmpty()) {
                val ndefMessage = ndefMessages[0] //as NdefMessage
                val ndefRecords = ndefMessage.records

                if (ndefRecords.isNotEmpty()) {
                    val attendanceCodeRecord = ndefRecords[0]
                    val courseCodeRecord = ndefRecords[1]

                    if (attendanceCodeRecord.tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(
                            attendanceCodeRecord.type,
                            NdefRecord.RTD_TEXT
                        )
                    ) {
                        try {
                            val attendancePayload = attendanceCodeRecord.payload
                            val coursePayload = courseCodeRecord.payload

                            val attendanceTextEncoding = if ((attendancePayload[0].toInt() and 128) == 0) "UTF-8" else "UTF-16"
                            val attendanceLanguageCodeLength = attendancePayload[0].toInt() and 51
                            val attendanceCode = String(
                                attendancePayload,
                                attendanceLanguageCodeLength + 1,
                                attendancePayload.size - attendanceLanguageCodeLength - 1,
                                Charset.forName(attendanceTextEncoding)
                            )

                            val courseTextEncoding = if ((coursePayload[0].toInt() and 128) == 0) "UTF-8" else "UTF-16"
                            val courseLanguageCodeLength = coursePayload[0].toInt() and 51
                            val courseCode = String(
                                coursePayload,
                                courseLanguageCodeLength + 1,
                                coursePayload.size - courseLanguageCodeLength - 1,
                                Charset.forName(courseTextEncoding)
                            )

                            // Update the attendance code TextView with the code read from the NFC tag
                            findViewById<TextView>(R.id.attendance_code_read).text = attendanceCode
                            findViewById<TextView>(R.id.course_code).text = courseCode
                        } catch (ex: UnsupportedEncodingException) {
                            Log.e(TAG, "Failed to decode NFC payload", ex)
                        }
                    } else {
                        Log.w(TAG, "Unsupported NFC record type or format")
                    }
                } else {
                    Log.w(TAG, "Empty NFC message received")
                }
            } else {
                Log.w(TAG, "No NDEF messages found")
            }
        }
    }



    override fun onPause() {
        if (this.isFinishing) {
            nfcAdapter?.disableForegroundDispatch(this)
        }
        super.onPause()
    }
}