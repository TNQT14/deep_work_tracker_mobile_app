package com.deepworktracker.data.mapper

import com.deepworktracker.data.database.entity.CategoryRuleEntity
import com.deepworktracker.domain.model.CategoryRule

class CategoryRuleMapper {
    fun toDomain(entity: CategoryRuleEntity): CategoryRule {
        return CategoryRule(
            id = entity.id,
            keyword = entity.keyword,
            startHour = entity.startHour,
            endHour = entity.endHour,
            category = entity.category,
            tag = entity.tag,
            priority = entity.priority
        )
    }

    fun toEntity(domain: CategoryRule): CategoryRuleEntity {
        return CategoryRuleEntity(
            id = domain.id,
            keyword = domain.keyword,
            startHour = domain.startHour,
            endHour = domain.endHour,
            category = domain.category,
            tag = domain.tag,
            priority = domain.priority
        )
    }
}

