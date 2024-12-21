package com.app.smartcar.data.repository

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class DefaultRealtimeDatabaseRepositoryTest {
    private lateinit var dbRef: FirebaseDatabase
    private lateinit var repository: DefaultRealtimeDatabaseRepository

    @Before
    fun setup() {
        // Mock Firebase options
        val options = FirebaseOptions.Builder()
            .setApplicationId("1:723263578329:android:891f5cf53140eb81c11ec8") // Replace with valid values
            .setApiKey("AIzaSyClzMEjNZt8qAkxWwfYUg4qNJSzzEsrhBM") // Fake API key
            .setDatabaseUrl("https://appdieukhienxetuxa-default-rtdb.firebaseio.com") // Fake DB URL
            .setStorageBucket("appdieukhienxetuxa.firebasestorage.app")
            .setProjectId("appdieukhienxetuxa")
            .build()

        // Initialize FirebaseApp
        FirebaseApp.initializeApp(Mockito.mock(Context::class.java), options)

        // Get a reference to the Firebase database
        dbRef = FirebaseDatabase.getInstance()

        // Initialize your repository
        repository = DefaultRealtimeDatabaseRepository(dbRef.getReference())
    }

    @Test
    fun `test fetch once`() {
        repository.fetchDataOnce("canhbaosinhantrai")
    }
}