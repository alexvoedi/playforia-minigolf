package org.moparforia.server.game;

import org.jboss.netty.channel.Channel;
import org.moparforia.shared.Tools;

public class Player {

    public static final int ACCESSLEVEL_NORMAL = 0; // todo: enum this ?
    public static final int ACCESSLEVEL_SHERIFF = 1;
    public static final int ACCESSLEVEL_ADMIN = 2;

    private Channel channel;
    private final int id;

    private String nick;
    private String locale;
    private String profileUrl;
    private String avatarUrl;
    private String clan;
    private int accessLevel;
    private int points;
    private boolean emailVerified;// todo or something like that maybe, find out
    private boolean registered;
    private boolean vip;
    private boolean sheriff;
    private boolean notAcceptingChallenges;
    private boolean isChatHidden;
    private boolean hasSkipped;

    private Game game;
    private Lobby lobby;
    private GameType gameType;

    public Player(Channel channel, int id) {
        this.channel = channel;
        this.id = id;
        points = 0;
        resetVals();
    }

    /**
     * USE WITH CARE!!11 LOL
     *
     * @see {java.lang.String}
     */
    public void resetVals() {
        nick = "-";
        locale = "-";
        profileUrl = "-";
        avatarUrl = "-";
        clan = "-";
        accessLevel = ACCESSLEVEL_NORMAL;
        points = 10000;
        emailVerified = false;
        registered = false;
        vip = false;
        sheriff = accessLevel == ACCESSLEVEL_SHERIFF || accessLevel == ACCESSLEVEL_ADMIN;
        notAcceptingChallenges = false;
        isChatHidden = false;
        hasSkipped = false;
        lobby = null;
        game = null;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public int getId() {
        return id;
    }

    public boolean hasSkipped() {
        return hasSkipped;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String url) {
        this.profileUrl = url;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String url) {
        this.avatarUrl = url;
    }

    public void setSkipped(boolean skipped) {
        this.hasSkipped = skipped;
    }

    public String getClan() {
        return clan;
    }

    public void setClan(String clan) {
        this.clan = clan;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int whatsyourgame) {
        this.accessLevel = whatsyourgame;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean b) {
        emailVerified = b;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean b) {
        registered = b;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean b) {
        vip = b;
    }

    public boolean isSheriff() {
        return sheriff;
    }

    public void setSheriff(boolean b) {
        sheriff = b;
    }

    public boolean isNotAcceptingChallenges() {
        return notAcceptingChallenges;
    }

    public void setNotAcceptingChallenges(boolean b) {
        notAcceptingChallenges = b;
    }

    public boolean isChatHidden() {
        return isChatHidden;
    }

    public void setChatHidden(boolean b) {
        isChatHidden = b;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby id) {
        this.lobby = id;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof Player))
            return false;
        Player p = (Player) o;
        return nick.equals(p.nick) && points == p.points && locale.equals(p.locale);
    }

    public String toString() {
        String tmp = (registered ? "r" : "") + (vip ? "v" : "") + (sheriff ? "s" : "") + (notAcceptingChallenges ? "n" : "");
        return Tools.triangelize(
                "3:" + (nick != null ? nick : ""),
                tmp.equals("") ? "w" : tmp,
                points,
                locale != null ? locale : "",
                profileUrl != null ? profileUrl : "",
                avatarUrl != null ? avatarUrl : ""
        );
    }
}
