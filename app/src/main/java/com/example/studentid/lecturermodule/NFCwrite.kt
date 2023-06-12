package com.example.studentid.lecturermodule

//import android.R.id.message
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.studentid.R
import com.example.studentid.R.style.MyAlertDialogStyle
import android.util.Log


//import com.example.studentid.SelectRole

//import com.example.studentid.SelectRole

//import kotlinx.android.synthetic.main.activity_write_data.*

private const val TAG = "NFCread"
class NFCwrite : AppCompatActivity() {
    private var intentFiltersArray: Array<IntentFilter>? = null
    private val techListsArray = arrayOf(arrayOf(NfcA::class.java.name))
    private val nfcAdapter: NfcAdapter? by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }
    private var pendingIntent: PendingIntent? = null
    private lateinit var btnback: Button
    private lateinit var continueWithAttendance: Button
    private lateinit var etcoursecodetxt: TextView
    private lateinit var etattendancecodetxt: TextView
    private var isPayloadWrote = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.lecturer_nfc_write)

        etcoursecodetxt = findViewById(R.id.writecoursecode)
        etattendancecodetxt = findViewById(R.id.generated_attendance_code)
        btnback = findViewById(R.id.btnback)
        btnback.setOnClickListener {
            val nfcreadintent = Intent(this, LecturerNFCread::class.java )
            startActivity(nfcreadintent)
        }

        continueWithAttendance = findViewById(R.id.continue_with_lec_attendance)
        continueWithAttendance.setOnClickListener {
            if (isPayloadWrote) {
                val lecturerattendanceintent = Intent(this, LecturerAttendance::class.java)
                lecturerattendanceintent.putExtra("attendanceCode", etattendancecodetxt.text.toString())
                lecturerattendanceintent.putExtra("courseCode", etcoursecodetxt.text.toString())
                startActivity(lecturerattendanceintent)
                finish()
            } else {
                // Handle the case when the payload is not yet read
                Toast.makeText(this, "Payload is not yet read", Toast.LENGTH_SHORT).show()
            }

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
            val builder = AlertDialog.Builder(this@NFCwrite, MyAlertDialogStyle)
            builder.setMessage("This device doesn't support NFC.")
            builder.setPositiveButton("Cancel", null)
            val myDialog = builder.create()
            myDialog.setCanceledOnTouchOutside(false)
            myDialog.show()
            // txttext.setText("THIS DEVICE DOESN'T SUPPORT NFC. PLEASE TRY WITH ANOTHER DEVICE!")
        } else if (!nfcAdapter!!.isEnabled) {
            val builder = AlertDialog.Builder(this@NFCwrite, MyAlertDialogStyle)
            builder.setTitle("NFC Disabled")
            builder.setMessage("Please Enable NFC")
            // txttext.setText("NFC IS NOT ENABLED. PLEASE ENABLE NFC IN SETTINGS->NFC")
            builder.setPositiveButton("Settings") { _, _ -> startActivity(Intent(Settings.ACTION_NFC_SETTINGS)) }
            builder.setNegativeButton("Cancel", null)
            val myDialog = builder.create()
            myDialog.setCanceledOnTouchOutside(false)
            myDialog.show()
        }
    }


    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        try {

            fun generateRandomCode(length: Int): String {
                val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') // characters to include in the code
                return (1..length)
                    .map { allowedChars.random() } // randomly select characters from allowedChars
                    .joinToString("") // join the characters to form the code
            }
            val coursecode = etcoursecodetxt.text.toString()
            if (coursecode.isBlank()) {
                throw Exception("Course Code can't be blank")
            }

            val attendanceCode = etattendancecodetxt
            val generatedCode = generateRandomCode(10) // call your random code generator function
            attendanceCode.text = generatedCode
            Log.d(TAG, "attendance code generated")
            Log.d(TAG, attendanceCode.text.toString())


            if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action
                    || NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action
                ) {
                val tag = intent.parcelable<Tag>(NfcAdapter.EXTRA_TAG) ?: return
                val ndef = Ndef.get(tag) ?: return

                if (ndef.isWritable) {

                        val message = NdefMessage(
                            arrayOf(
                                NdefRecord.createTextRecord("en", generatedCode),
                                NdefRecord.createTextRecord("en", coursecode),
                            )
                        )
                        ndef.connect()
                        ndef.writeNdefMessage(message)
                        ndef.close()
                        Toast.makeText(applicationContext, "Successfully Wrote!", Toast.LENGTH_SHORT)
                            .show()
                        isPayloadWrote = true


                    }
                }


        }
        catch (Ex:Exception)
        {
            Toast.makeText(applicationContext, Ex.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onPause() {
        if (this.isFinishing) {
            nfcAdapter?.disableForegroundDispatch(this)
        }
        super.onPause()
    }



}