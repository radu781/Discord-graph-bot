package discord_bot.model.searcher;

import java.util.List;
import org.json.simple.JSONObject;

import discord_bot.utils.exceptions.JSONParseException;

public interface Searcher {
    public void setSite(String site);

    public JSONObject searchPages(String query) throws JSONParseException;

    public List<JSONObject> searchTitle(List<String> allQueries) throws JSONParseException;
}
