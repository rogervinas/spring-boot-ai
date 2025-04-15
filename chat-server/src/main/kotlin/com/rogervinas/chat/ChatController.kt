package com.rogervinas.chat

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatController(private val chatService: ChatService) {

    @PostMapping("/{chatId}/chat")
    fun chat(@PathVariable chatId: String, @RequestParam question: String): String? {
        return chatService.chat(chatId, question)
    }
}
