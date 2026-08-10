package com.deepworktracker.data.di

import javax.inject.Qualifier

/** Marks the DataStore backing Focus Shield, kept separate from the user-preferences store. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FocusShieldDataStore
