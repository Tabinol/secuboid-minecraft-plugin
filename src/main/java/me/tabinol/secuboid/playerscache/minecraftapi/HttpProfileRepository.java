/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.secuboid.playerscache.minecraftapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HttpProfileRepository {

    // You're not allowed to request more than 100 profiles per go.
    private static final int PROFILES_PER_REQUEST = 100;

    private final String agent;
    private HttpClient client;

    public HttpProfileRepository(String agent) {
        this(agent, HttpClient.getInstance());
    }

    private HttpProfileRepository(String agent, HttpClient client) {
        this.agent = agent;
        this.client = client;
    }

    public Profile[] findProfilesByNames(String... names) {

        List<Profile> profiles = new ArrayList<Profile>();
        try {

            List<HttpHeader> headers = new ArrayList<HttpHeader>();
            headers.add(new HttpHeader("Content-Type", "application/json"));

            int namesCount = names.length;
            int start = 0;
            int i = 0;
            do {
                int end = PROFILES_PER_REQUEST * (i + 1);
                if (end > namesCount) {
                    end = namesCount;
                }
                List<String> namesBatch = new ArrayList<String>();
                namesBatch.addAll(Arrays.asList(names).subList(start, end));
                String body = JSONArray.toJSONString(namesBatch);
                profiles.addAll(post(getProfilesUrl(), body, headers));

                start = end;
                i++;
            } while (start < namesCount);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return profiles.toArray(new Profile[profiles.size()]);
    }

    private URL getProfilesUrl() throws MalformedURLException {
        // To lookup Minecraft profiles, agent should be "minecraft"
        return new URL("https://api.mojang.com/profiles/" + agent);
    }

    private Collection<Profile> post(URL url, String body, List<HttpHeader> headers) throws IOException, ParseException {

        String response = client.post(url, body, headers);
        JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray) parser.parse(response);
        List<Profile> profiles = new ArrayList<Profile>();
        for (Object object : array) {
            profiles.add(new Profile(((JSONObject) object).get("id").toString(), ((JSONObject) object).get("name").toString()));
        }
        return profiles;
    }
}
