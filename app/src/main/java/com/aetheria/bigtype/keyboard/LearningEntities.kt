package com.aetheria.bigtype.keyboard

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "autocorrect_rules")
data class AutocorrectRuleEntity(
    @PrimaryKey val fromWord: String,
    val toWord: String,
    val appId: String? = null,
    val frequency: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val isUserAdded: Boolean = false
)

@Entity(tableName = "emoji_predictions")
data class EmojiPredictionEntity(
    @PrimaryKey val trigger: String,
    val emoji: String,
    val frequency: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface AutocorrectDao {
    @Query("SELECT * FROM autocorrect_rules WHERE fromWord = :word ORDER BY frequency DESC LIMIT 1")
    suspend fun getByWord(word: String): AutocorrectRuleEntity?

    @Query("SELECT * FROM autocorrect_rules ORDER BY frequency DESC LIMIT :limit")
    suspend fun getTopByFreq(limit: Int = 20): List<AutocorrectRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: AutocorrectRuleEntity)

    @Query("UPDATE autocorrect_rules SET frequency = frequency + 1 WHERE fromWord = :word")
    suspend fun incrementFreq(word: String)

    @Query("UPDATE autocorrect_rules SET frequency = MAX(0, frequency - 1) WHERE fromWord = :word")
    suspend fun decrementFreq(word: String)

    @Query("DELETE FROM autocorrect_rules WHERE fromWord = :word AND frequency = 0")
    suspend fun purgeZeroFreq(word: String)
}

@Dao
interface EmojiPredictionDao {
    @Query("SELECT * FROM emoji_predictions WHERE trigger = :trigger ORDER BY frequency DESC")
    suspend fun getByTrigger(trigger: String): List<EmojiPredictionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prediction: EmojiPredictionEntity)

    @Query("UPDATE emoji_predictions SET frequency = frequency + 1 WHERE trigger = :trigger")
    suspend fun incrementFreq(trigger: String)
}
