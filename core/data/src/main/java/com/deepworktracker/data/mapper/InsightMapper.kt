package com.deepworktracker.data.mapper

import com.deepworktracker.common.datetime.toEpochMilliseconds
import com.deepworktracker.data.database.entity.InsightEntity
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType
import kotlinx.datetime.Instant
import org.json.JSONObject

class InsightMapper {
    
    fun toDomain(entity: InsightEntity): Insight {
        val data = entity.data?.let {
            try {
                val json = JSONObject(it)
                json.keys().asSequence().associateWith { key ->
                    json.get(key) as Any
                }
            } catch (e: Exception) {
                null
            }
        }
        
        return Insight(
            id = entity.id,
            type = InsightType.valueOf(entity.type),
            message = entity.message,
            generatedAt = Instant.fromEpochMilliseconds(entity.generatedAt),
            confidence = entity.confidence,
            data = data
        )
    }
    
    fun toEntity(domain: Insight): InsightEntity {
        val dataJson = domain.data?.let {
            try {
                JSONObject(it).toString()
            } catch (e: Exception) {
                null
            }
        }
        
        return InsightEntity(
            id = domain.id,
            type = domain.type.name,
            message = domain.message,
            generatedAt = domain.generatedAt.toEpochMilliseconds(),
            confidence = domain.confidence,
            data = dataJson
        )
    }
}
