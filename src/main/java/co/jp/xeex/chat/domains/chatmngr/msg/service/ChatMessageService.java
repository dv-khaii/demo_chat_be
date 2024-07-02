package co.jp.xeex.chat.domains.chatmngr.msg.service;

import co.jp.xeex.chat.domains.chat.ChatMessageDto;
import co.jp.xeex.chat.domains.chatmngr.msg.dto.ChatMessageDetailDto;
import co.jp.xeex.chat.domains.chatmngr.msg.dto.RepplyMessageDetailDto;
import co.jp.xeex.chat.domains.file.dto.FileDto;
import co.jp.xeex.chat.entity.ChatMessage;
import co.jp.xeex.chat.exception.BusinessException;

import java.util.List;

/**
 * ChatMessageService
 * 
 * @author q_thinh
 */
public interface ChatMessageService {

    /**
     * Retrieves a ChatMessageDto object by its ID.
     *
     * @param messageId the ID of the chat message
     * @param requestBy the empCd
     * @return the ChatMessageDto object
     */
    public ChatMessageDto getChatMessageDtoById(String messageId, String requestBy);

    /**
     * Retrieves a ChatMessageDto object by its detail object.
     *
     * @param chatMessageDetailDto the detail object of the chat message
     * @param requestBy            the empCd
     * @return the ChatMessageDto object
     */
    public ChatMessageDto getChatMessageDtoByDetailObj(ChatMessageDetailDto chatMessageDetailDto, String requestBy);

    /**
     * Retrieves a RepplyMessageDetailDto object by its ID.
     *
     * @param repplyMessageId the ID of the reply message
     * @param requestBy       the empCd
     * @return the RepplyMessageDetailDto object
     */
    public RepplyMessageDetailDto getRepplyMessageDetail(String repplyMessageId, String requestBy);

    /**
     * Saves chat files associated with a chat message.
     *
     * @param chatMessageDto the chat message DTO
     * @throws BusinessException if an error occurs while saving the files
     */
    public void saveChatFile(ChatMessageDto chatMessageDto) throws BusinessException;

    /**
     * Retrieves a list of file DTOs associated with a chat message.
     *
     * @param messageId the ID of the chat message
     * @return the list of file DTOs
     */
    public List<FileDto> getChatFileDto(String messageId);

    /**
     * Deletes or edits a chat message.
     *
     * @param chatMessage the chat message object
     * @param lang        the language code
     */
    public void deleteOrEditChatMessage(ChatMessage chatMessage, String lang);
}
