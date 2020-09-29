package com.example.contacts

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    fun Context.fetchAllContacts(): List<ContactAdapter.Contact> {
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
            .use { cursor ->
                if (cursor == null) return emptyList()
                val builder = ArrayList<ContactAdapter.Contact>()
                while (cursor.moveToNext()) {
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                            ?: "N/A"
                    val phoneNumber =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            ?: "N/A"

                    builder.add(ContactAdapter.Contact(name, phoneNumber))
                }
                return builder
            }
    }

    var myRequestId = 1

    fun startWorking() {
        val viewManager = LinearLayoutManager(this)
        contactList = fetchAllContacts()
        val contactFound = resources.getQuantityString(
            R.plurals.numberOfContactsFound,
            contactList.size,
            contactList.size
        )

        Toast.makeText(this@MainActivity, contactFound, Toast.LENGTH_SHORT).show()
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = ContactAdapter(contactList) {
                myRequestId = 2
                var callIsGranted = false
                if (ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.CALL_PHONE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        myRequestId
                    )
                } else {
                    callIsGranted = true
                }
                if (callIsGranted) {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_DIAL
                        data = Uri.parse("tel:" + it.phoneNumber)
                    }
                    startActivity(sendIntent)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            myRequestId -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("5", "granted is true")
                    if (myRequestId == 1) {
                        startWorking()
                    }
                }
                return
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        var granted = false
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, // Контекст
                arrayOf(Manifest.permission.READ_CONTACTS),
                myRequestId
            )
        } else {
            granted = true
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (granted) {
            startWorking()
        }
    }
}