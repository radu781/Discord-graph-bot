package discord_bot.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import discord_bot.model.TopicModel;
import discord_bot.model.searcher.Searcher;
import discord_bot.model.searcher.StackExchangeSearcher;
import discord_bot.model.searcher.WikipediaSearcher;
import discord_bot.utils.exceptions.ControllerArgException;
import discord_bot.utils.exceptions.JSONParseException;
import discord_bot.view.Topic;

@RestController
public class TopicController {
    @Autowired
    private TopicModel topicModel;

    @GetMapping("/search/wiki")
    public ResponseEntity<Topic> getWikiInfo(
            @RequestParam("q") String query,
            @RequestParam("index") int index) {
        Topic myModel = new Topic();
        topicModel.setSearcher(new WikipediaSearcher());
        try {
            myModel = topicModel.searchResultByIndex(query, index, true);
        } catch (JSONParseException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (ArrayIndexOutOfBoundsException e) {
            myModel.setTitle("Index out of range, expected 0 <= index < " + e.getMessage());
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(myModel);
        }
        return ResponseEntity.status(HttpStatus.OK).body(myModel);
    }

    @GetMapping("/search/stack")
    public ResponseEntity<Topic> getStackInfo(
            @RequestParam("q") String query,
            @RequestParam("source") String source,
            @RequestParam("index") int index) {
        Topic myModel = new Topic();
        try {
            parseStackInfo(query, source, index);
        } catch (ControllerArgException e) {
            myModel.setTitle(e.getMessage());
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(myModel);
        }

        Searcher searcher = new StackExchangeSearcher();
        searcher.setSite(source);
        topicModel.setSearcher(searcher);
        try {
            myModel = topicModel.searchResultByIndex(query, index, true);
        } catch (JSONParseException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (ArrayIndexOutOfBoundsException e) {
            myModel.setTitle("Index out of range, expected 0 <= index < " + e.getMessage());
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(myModel);
        }
        return ResponseEntity.status(HttpStatus.OK).body(myModel);
    }

    private void parseStackInfo(String query, String source, int index) throws ControllerArgException {
        String[] supportedStrings = new String[] { "stackoverflow", "mathoverflow.net" };
        if (!Arrays.asList(supportedStrings).contains(source)) {
            throw new ControllerArgException("Source not found");
        }
    }
}
