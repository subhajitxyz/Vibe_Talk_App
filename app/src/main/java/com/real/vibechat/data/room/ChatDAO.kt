package com.real.vibechat.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.real.vibechat.presentation.chat.MessageStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDAO {

    @Query("SELECT * FROM messages WHERE chatRoomId = :chatRoomId ORDER BY sentAt DESC")
    fun observeMessages(chatRoomId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE id = :id")
    fun getMessageById(id: String): MessageEntity

    @Query("UPDATE messages SET messageStatus = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: MessageStatus)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<MessageEntity>)

    @Update
    suspend fun update(message: MessageEntity)




    // Operation related to chat rooms

    @Query("SELECT EXISTS(SELECT 1 FROM chat_rooms WHERE chatRoomId = :chatRoomId)")
    suspend fun chatRoomExists(chatRoomId: String): Boolean

    @Query("SELECT * FROM chat_rooms ORDER BY sentAt DESC")
    fun observeChatRooms(): Flow<List<ChatRoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoom(chatRoomEntity: ChatRoomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllChatRooms(messages: List<ChatRoomEntity>)

    @Query("""
    UPDATE chat_rooms
    SET sentAt = :sentAt,
        messageId = :messageId,
        lastMessage = :lastMessage
    WHERE chatRoomId = :chatRoomId
""")
    suspend fun updateLastMessage(
        chatRoomId: String,
        lastMessage: String,
        messageId: String,
        sentAt: Long?
    )


    @Update
    suspend fun updateChatRoom(chatRoomEntity: ChatRoomEntity)







}