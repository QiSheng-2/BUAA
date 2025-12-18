/*
package com.example.chatroom.controller;

import com.example.chatroom.entity.Message;
import com.example.chatroom.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@RestController
//@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    //@GetMapping("/search")
    public ResponseEntity<Page<Message>> search(
            @RequestParam String room,
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            // In a real app, get userId from SecurityContext
            @RequestParam(required = false) String userId
    ) {
        Page<Message> result = searchService.searchInRoom(room, q, page, size, userId);
        return ResponseEntity.ok(result);
    }
}
*/