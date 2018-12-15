package com.eric.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by eric on 2017/9/25.
 */
@Service
public class NoticeUtil {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 指定人发送
     *
     * @param username
     * @param content
     */
    public void sendNoticeToUser(String username, String content) {
        messagingTemplate.convertAndSendToUser(username, "/topic/broadcast", content);
    }
}
