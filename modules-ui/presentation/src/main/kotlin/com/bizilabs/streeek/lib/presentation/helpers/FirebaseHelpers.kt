package com.bizilabs.streeek.lib.presentation.helpers

import android.app.Activity
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import timber.log.Timber

fun Activity.initFirebaseMessaging() {
    val topics = listOf("updates")
    for (topic in topics) Firebase.messaging.subscribeToTopic(topic).addOnCompleteListener {
        if (it.isSuccessful) {
            Timber.d("Successfully subscribed to topic: $topic")
        } else {
            Timber.d("Failed to subscribe to topic: $topic")
        }
    }
}
