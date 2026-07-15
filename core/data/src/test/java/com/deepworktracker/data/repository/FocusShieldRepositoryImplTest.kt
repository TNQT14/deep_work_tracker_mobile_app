package com.deepworktracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.deepworktracker.data.preferences.FocusShieldLocalDataSource
import com.deepworktracker.domain.model.FocusShieldConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Drives [FocusShieldRepositoryImpl] over a real temp-file DataStore so the per-key edit logic
 * (not just delegation) is exercised. No Android Context / Robolectric needed — DataStore
 * Preferences is pure JVM IO.
 */
class FocusShieldRepositoryImplTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val dataStoreScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var repository: FocusShieldRepositoryImpl

    @Before
    fun setUp() {
        val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
            scope = dataStoreScope,
            produceFile = { tmpFolder.newFile("focus_shield.preferences_pb") },
        )
        repository = FocusShieldRepositoryImpl(FocusShieldLocalDataSource(dataStore))
    }

    @After
    fun tearDown() {
        dataStoreScope.cancel()
    }

    @Test
    fun `empty store returns default config`() = runTest {
        assertEquals(FocusShieldConfig.DEFAULT, repository.getConfig())
        assertNull(repository.getPreviousDndFilter())
    }

    @Test
    fun `setDndEnabled persists`() = runTest {
        repository.setDndEnabled(true)
        assertTrue(repository.getConfig().dndEnabled)
    }

    @Test
    fun `setBlocklist round-trips the set`() = runTest {
        val packages = setOf("com.facebook.katana", "com.instagram.android")
        repository.setBlocklist(packages)
        assertEquals(packages, repository.getConfig().blocklist)
    }

    @Test
    fun `setPreviousDndFilter stores then clears on null`() = runTest {
        repository.setPreviousDndFilter(3)
        assertEquals(3, repository.getPreviousDndFilter())

        repository.setPreviousDndFilter(null)
        assertNull(repository.getPreviousDndFilter())
    }

    @Test
    fun `changing dnd does not clobber blocklist`() = runTest {
        val packages = setOf("com.example.distraction")
        repository.setBlocklist(packages)

        repository.setDndEnabled(true)

        val config = repository.getConfig()
        assertTrue(config.dndEnabled)
        assertEquals(packages, config.blocklist)
    }
}
