package discord_bot.model.searcher;

import java.util.List;

import org.json.simple.JSONObject;

import discord_bot.utils.exceptions.JSONParseException;

public class StackSearcher implements Searcher {
    @Override
    public JSONObject searchPages(String query) throws JSONParseException {
        return null;
    }

    @Override
    public List<JSONObject> searchTitle(List<String> allQueries) throws JSONParseException {
        return null;
    }
}
