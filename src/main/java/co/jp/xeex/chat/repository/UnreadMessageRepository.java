package co.jp.xeex.chat.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.jp.xeex.chat.base.RepositoryBase;
import co.jp.xeex.chat.entity.UnreadMessage;

/**
 * Repository for unread messages
 * 
 * @author v_long
 */
@Repository
public interface UnreadMessageRepository extends RepositoryBase<UnreadMessage, String> {
    /**
     * get unread message by userId, chatGroupId and repplyMessageId
     * 
     * @param userId          the user id
     * @param chatGroupId     the chat group id
     * @param repplyMessageId the repply message id
     * @return the unread message of repply by the userId, chatGroupId and
     *         repplyMessageId
     */
    @Query("SELECT o " +
            " FROM " +
            "   UnreadMessage o " +
            " WHERE " +
            "   userId = :userId " +
            "   AND chatGroupId = :chatGroupId " +
            "   AND ( (:repplyMessageId IS NULL AND repplyMessageId IS NULL) " +
            "      OR (:repplyMessageId IS NOT NULL AND repplyMessageId = :repplyMessageId ) ) ")
    UnreadMessage getUnreadMessage(@Param("userId") String userId,
            @Param("chatGroupId") String chatGroupId,
            @Param("repplyMessageId") String repplyMessageId);

    /**
     * Retrieves the total number of unread messages with a reply for a specific
     * user and chat group.
     *
     * @param userId      the ID of the user
     * @param chatGroupId the ID of the chat group
     * @return the total number of unread messages with a reply
     */
    @Query("SELECT " +
            "   SUM(o.unreadCount) " +
            " FROM " +
            "   UnreadMessage o " +
            " WHERE " +
            "   userId = :userId " +
            "   AND chatGroupId = :chatGroupId " +
            "   AND repplyMessageId IS NOT NULL ")
    Integer getUnreadCountRepply(@Param("userId") String userId, @Param("chatGroupId") String chatGroupId);

}