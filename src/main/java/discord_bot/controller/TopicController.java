package discord_bot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import discord_bot.model.TopicModel;
import discord_bot.utils.exceptions.JSONParseException;
import discord_bot.view.Topic;

@RestController
public class TopicController {
    @Autowired
    private TopicModel topicModel;

    @GetMapping("/search")
    public ResponseEntity<Topic> getInfo(@RequestParam("q") String query, @RequestParam("index") int index) {
        Topic myModel;
        try {
            myModel = topicModel.searchResultByIndex(query, 0);
        } catch (JSONParseException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(myModel);
    }
}
