package discord_bot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import discord_bot.model.TopicModel;
import discord_bot.view.Topic;

@RestController
public class TopicController {
    @Autowired
    private TopicModel topicModel;

    @GetMapping("/search")
    public ResponseEntity<Topic> getInfo(@RequestParam("q") String query, @RequestParam("index") int index) {
        Topic myModel = topicModel.search(query);
        for (int count = 0; count < index - 1; count++) {
            if (myModel == null) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
            }
            myModel = topicModel.search(query);
        }
        return ResponseEntity.status(HttpStatus.OK).body(myModel);
    }
}
