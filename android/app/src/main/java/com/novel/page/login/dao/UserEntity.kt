package com.novel.page.login.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户信息数据实体
 * 
 * Room数据库用户表的实体类，存储用户基本信息
 * 支持本地缓存和离线访问
 * 
 * @property uid 用户唯一标识，作为主键
 * @property nickName 用户昵称
 * @property userPhoto 用户头像URL
 * @property userSex 用户性别（0:未知, 1:男, 2:女）
 * @property lastUpdateTime 最后更新时间戳，用于数据同步和缓存管理
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val nickName: String,
    val userPhoto: String,
    val userSex: Int,
    val lastUpdateTime: Long = System.currentTimeMillis() // 添加最后更新时间
)
