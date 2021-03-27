package com.sender.authcode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MessageReceiver : BroadcastReceiver() {

    @Suppress("DEPRECATION")
    override fun onReceive(context: Context, intent: Intent) {
        var number: String = ""
        var content: String = ""
        Calendar.getInstance()
        val saoPauloDate = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"))
        val formattedDate = StringBuffer()
        var dayInt = saoPauloDate.get(Calendar.DAY_OF_MONTH)
        if (dayInt < 10) {
            val day = "0" + dayInt.toString()
            formattedDate.append(day).append("/")
        } else {
            val day = dayInt.toString()
            formattedDate.append(day).append("/")
        }
        val monthInt = saoPauloDate.get(Calendar.MONTH)
        if (monthInt < 10) {
            val monthAux = monthInt + 1
            val month = "0" + monthAux.toString()
            formattedDate.append(month).append("/")
        } else {
            val monthAux = monthInt + 1
            val month = monthAux.toString()
            formattedDate.append(month).append("/")
        }
        formattedDate.append(saoPauloDate.get(Calendar.YEAR)).append("")
        var date = formattedDate.toString()
        val pdus = intent.getExtras()?.get("pdus") as Array<Any>
        val msgs = arrayOfNulls<SmsMessage>(pdus.size)
        for (i in msgs.indices) {
            // Constrói a mensagem SMS
            msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
            // Obtém o número do remetente
            number += msgs[i]?.getOriginatingAddress()
            // Obtém o corpo da mensagem (texto)
            val contentAux = msgs[i]?.getMessageBody().toString()
            val contentArray = contentAux.chunked(1)
            for (i in contentArray) {
                val j = i.toIntOrNull()
                if (j != null) {
                    content += j
                }
            }
        }
        if(number == "<SENDER NUMBER GOES HERE>") {
            val postUrl = "<API URL GOES HERE>"
            val requestQueue = Volley.newRequestQueue(context)
            val postData = JSONObject()
            try {
                postData.put("number", number)
                postData.put("content", content)
                postData.put("date", date)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST,
                    postUrl,
                    postData,
                    { response -> Log.d("SMSAPI", response.toString()) }
            ) { error -> Log.d("SMSAPI", error.toString()) }
            requestQueue.add(jsonObjectRequest)
        }
    }
}
