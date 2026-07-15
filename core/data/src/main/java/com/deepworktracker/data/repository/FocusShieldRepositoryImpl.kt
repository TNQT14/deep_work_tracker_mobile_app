package com.deepworktracker.data.repository

import com.deepworktracker.data.preferences.FocusShieldLocalDataSource
import com.deepworktracker.domain.model.FocusShieldConfig
import com.deepworktracker.domain.repository.FocusShieldRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusShieldRepositoryImpl @Inject constructor(
    private val localDataSource: FocusShieldLocalDataSource,
) : FocusShieldRepository {

    override fun observeConfig(): Flow<FocusShieldConfig> = localDataSource.observeConfig()

    override suspend fun getConfig(): FocusShieldConfig = localDataSource.getConfig()

    override suspend fun setDndEnabled(enabled: Boolean): Result<Unit> =
        localDataSource.setDndEnabled(enabled)

    override suspend fun setBlocklist(packages: Set<String>): Result<Unit> =
        localDataSource.setBlocklist(packages)

    override suspend fun getPreviousDndFilter(): Int? = localDataSource.getPreviousDndFilter()

    override suspend fun setPreviousDndFilter(filter: Int?): Result<Unit> =
        localDataSource.setPreviousDndFilter(filter)
}
