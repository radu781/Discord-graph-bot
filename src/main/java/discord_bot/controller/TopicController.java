package discord_bot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import discord_bot.model.TopicModel;
import discord_bot.model.searcher.Searcher;
import discord_bot.model.searcher.StackExchangeSearcher;
import discord_bot.model.searcher.WikipediaSearcher;
import discord_bot.utils.enums.SourceType;
import discord_bot.utils.exceptions.ControllerArgException;
import discord_bot.utils.exceptions.JSONParseException;
import discord_bot.view.Topic;

@RestController
public class TopicController {
    private TopicModel topicModel = new TopicModel();

    @GetMapping("/search/wiki")
    public ResponseEntity<Topic> getWikiInfoReadOnly(@RequestParam("q") String query,
            @RequestParam("index") int index) {
        return searchWiki(query, index, true);
    }

    @PostMapping("/search/wiki")
    public ResponseEntity<Topic> getWikiInfo(@RequestParam("q") String query,
            @RequestParam("index") int index) {
        return searchWiki(query, index, false);
    }

    @GetMapping("/search/stack")
    public ResponseEntity<Topic> getStackInfoReadOnly(@RequestParam("q") String query,
            @RequestParam("source") String source, @RequestParam("index") int index) {
        return searchStack(query, source, index, true);
    }

    @PostMapping("/search/stack")
    public ResponseEntity<Topic> getStackInfo(@RequestParam("q") String query,
            @RequestParam("source") String source, @RequestParam("index") int index) {
        return searchStack(query, source, index, false);
    }

    private ResponseEntity<Topic> searchWiki(String query, int index, boolean readOnly) {
        Topic myModel = new Topic();
        myModel.setSource(SourceType.WIKIPEDIA);
        Searcher searcher = new WikipediaSearcher();
        searcher.setType(SourceType.WIKIPEDIA);
        topicModel.setSearcher(searcher);
        topicModel.setReadOnly(readOnly);
        try {
            myModel = topicModel.getResultByIndex(query, index);
        } catch (JSONParseException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (ArrayIndexOutOfBoundsException e) {
            myModel.setTitle("Index out of range, expected 0 <= index < " + e.getMessage());
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(myModel);
        }
        return ResponseEntity.status(HttpStatus.OK).body(myModel);
    }

    private ResponseEntity<Topic> searchStack(String query, String source, int index, boolean readOnly) {
        Topic myModel = new Topic();
        myModel.setSource(SourceType.fromString(source));
        try {
            parseStackInfo(query, source, index);
        } catch (ControllerArgException e) {
            myModel.setTitle(e.getMessage());
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(myModel);
        }

        Searcher searcher = new StackExchangeSearcher();
        searcher.setSite(source);
        searcher.setType(SourceType.fromString(source));
        topicModel.setSearcher(searcher);
        topicModel.setReadOnly(readOnly);
        try {
            myModel = topicModel.getResultByIndex(query, index);
        } catch (JSONParseException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (ArrayIndexOutOfBoundsException e) {
            myModel.setTitle("Index out of range, expected 0 <= index < " + e.getMessage());
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(myModel);
        }
        return ResponseEntity.status(HttpStatus.OK).body(myModel);
    }

    private void parseStackInfo(String query, String source, int index) throws ControllerArgException {
        if (SourceType.fromString(source) == SourceType.UNKNOWN) {
            throw new ControllerArgException("Source not found");
        }
    }
}
