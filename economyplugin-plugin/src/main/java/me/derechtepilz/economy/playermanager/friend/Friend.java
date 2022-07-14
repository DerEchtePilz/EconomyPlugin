package me.derechtepilz.economy.playermanager.friend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;

public class Friend {
    private Map<String, List<String>> saveFriends = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File friends = new File(new File("./plugins/Economy"), "friends.json");

    public void addFriend(Player player, Player newFriend) {
        String playerUuid = String.valueOf(player.getUniqueId());
        String friendUuid = String.valueOf(newFriend.getUniqueId());
        List<String> friends;
        if (saveFriends.containsKey(playerUuid)) {
            friends = saveFriends.get(playerUuid);
            friends.add(friendUuid);
        } else {
            friends = new ArrayList<>();
            friends.add(friendUuid);
        }
        saveFriends.put(playerUuid, friends);
        try {
            saveFriends();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeFriend(Player player, OfflinePlayer oldFriend) {
        String playerUuid = String.valueOf(player.getUniqueId());
        String friendUuid = String.valueOf(oldFriend.getUniqueId());
        List<String> friends = saveFriends.get(playerUuid);
        friends.remove(friendUuid);
        saveFriends.put(playerUuid, friends);
        try {
            saveFriends();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasFriend(Player player) {
        return saveFriends.containsKey(String.valueOf(player.getUniqueId()));
    }

    public boolean isFriend(Player player, OfflinePlayer friend) {
        if (hasFriend(player)) {
            List<String> friends = saveFriends.get(String.valueOf(player.getUniqueId()));
            return friends.contains(String.valueOf(friend.getUniqueId()));
        }
        return false;
    }

    public List<String> getFriends(Player player) {
        List<String> friendNames = new ArrayList<>();
        if (hasFriend(player)) {
            for (String uuid : saveFriends.get(String.valueOf(player.getUniqueId()))) {
                friendNames.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
            }
        }
        return friendNames;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveFriends() throws IOException {
        if (!friends.exists()) {
            friends.createNewFile();
        }
        Writer writer = new FileWriter(friends);
        writer.write(gson.toJson(saveFriends));
        writer.close();
    }

    @SuppressWarnings({"unchecked", "unused"})
    public void loadFriends() throws IOException {
        saveFriends.clear();
        if (!friends.exists()) {
            return;
        }
        saveFriends = gson.fromJson(new FileReader(friends), HashMap.class);
    }
}
