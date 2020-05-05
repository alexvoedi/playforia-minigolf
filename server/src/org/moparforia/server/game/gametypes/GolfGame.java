package org.moparforia.server.game.gametypes;

import org.jboss.netty.channel.Channel;
import org.moparforia.server.Server;
import org.moparforia.server.game.Game;
import org.moparforia.server.game.Lobby;
import org.moparforia.server.game.LobbyType;
import org.moparforia.server.game.Player;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketType;
import org.moparforia.server.track.TrackManager;
import org.moparforia.shared.Tools;
import org.moparforia.shared.Track;

import java.util.ArrayList;
import java.util.regex.Matcher;

public abstract class GolfGame extends Game {

    public static final int STROKES_UNLIMITED = 0;
    public static final int STROKETIMEOUT_INFINITE = 0;
    public static final int WATER_START = 0;
    public static final int WATER_SHORE = 1;
    public static final int COLLSION_NO = 0;
    public static final int COLLSION_YES = 1;
    public static final int SCORING_STROKE = 0;
    public static final int SCORING_TRACK = 1;
    public static final int SCORING_WEIGHT_END_NONE = 0;
    public static final int SCORING_WEIGHT_END_LITTLE = 1;
    public static final int SCORING_WEIGHT_END_PLENTY = 2;

    protected int numberOfTracks;
    protected int perms;
    protected int tracksType;
    protected int maxStrokes;
    protected int strokeTimeout;
    protected int waterEvent;
    protected int collision;
    protected int trackScoring;
    protected int trackScoringEnd;
    protected int numPlayers;
    protected ArrayList<Track> tracks;
    protected int[] playerStrokes;

    protected int currentTrack = 0;
    protected int strokeCounter = 0;
    protected int strokesThisTrack = 0;
    protected String playStatus;

    public GolfGame(int gameId, LobbyType lobbyId, String name, String password, boolean passworded,
                    int numberOfTracks, int perms, int tracksType, int maxStrokes, int strokeTimeout,
                    int waterEvent, int collision, int trackScoring, int trackScoringEnd, int numPlayers) {
        super(gameId, lobbyId, name, password, passworded);
        this.numberOfTracks = numberOfTracks;
        this.perms = perms;
        this.tracksType = tracksType;
        this.maxStrokes = maxStrokes;
        this.strokeTimeout = strokeTimeout;
        this.waterEvent = waterEvent;
        this.collision = collision;
        this.trackScoring = trackScoring;
        this.trackScoringEnd = trackScoringEnd;
        this.numPlayers = numPlayers;
        this.playerStrokes = new int[numPlayers];
        tracks = initTracks();

    }


    public abstract ArrayList<Track> initTracks();

    @Override
    public boolean handlePacket(Server server, Player player, Matcher message) {
        if (message.group(1).equals("beginstroke")) {
            //beginstroke\t7sw8
            String mouseCoords = message.group(2);
            beginStroke(player, mouseCoords);

        } else if (message.group(1).equals("endstroke")) {
            endStroke(player, message.group(3));

            // 999 dirty hack here due to regex borking itself, keep "voteski"
        } else if (message.group(1).equals("voteskip") || message.group(1).equals("voteski")
                || message.group(1).equals("skip") || message.group(1).equals("ski")) {
            voteSkip(player);
        } else if (message.group(1).contains("rate")) {
            rateTrack(message.group(2));  //todo: fix the matcher bs, does not work properly here
        } else if (message.group(1).equals("newgame")) {
            wantsNewGame(player);
        } else if (message.group(1).equals("back")) {
            removePlayer(player);
            if (isEmpty()) {
                player.getLobby().removeGame(this);
            }

            player.getLobby().addPlayer(player, Lobby.JOIN_TYPE_FROMGAME);
            player.setGame(null);
        } else {
            return false;
        }
        return true;
    }

    @Override
    protected void reset() {
        currentTrack = 0;
        playerStrokes = new int[playerCount()];
        strokesThisTrack = 0;
        strokeCounter = 0;
        tracks = initTracks();
    }

    public void startGame() {
        writeAll(new Packet(PacketType.DATA, Tools.tabularize("game", "start")));
        StringBuilder buff = new StringBuilder(); // STRING BUILDER IS BEING USEDS FAGGOTS
        for (int i = 0; i < getPlayers().size(); i++) {
            buff.append("t");
        }
        playStatus = buff.toString().replace("t", "f");

        writeAll(new Packet(PacketType.DATA, Tools.tabularize("game", "resetvoteskip")));
        writeAll(new Packet(PacketType.DATA, Tools.tabularize("game", "starttrack", buff.toString(), gameId, tracks.get(0))));
        writeAll(new Packet(PacketType.DATA, Tools.tabularize("game", "startturn", 0)));
    }

    public void rateTrack(String rating) {
        tracks.get(currentTrack).rate(rating);
    }

    public void sendGameInfo(Player player) {
        Channel c = player.getChannel();
        c.write(new Packet(PacketType.DATA, Tools.tabularize("status", "game")));
        c.write(new Packet(PacketType.DATA, Tools.tabularize("game", "gameinfo",
                name, passworded ? "t" : "f", gameId, numPlayers, tracks.size(),
                tracksType, maxStrokes, strokeTimeout, waterEvent, collision, trackScoring,
                trackScoringEnd, "f")));
        //     Conn.writeD(session, "game", "gameinfo", name, "t", gameId, playerCount,
        //             numberOfTracks, trackType, maxStrokes, strokeTimeout, water, collision, scoreSystem, weightEnd, "f");
    }

    public void endStroke(Player p, String playStatus) {
        boolean finished = true;
        playerStrokes[getPlayerId(p)] += 1;
        this.playStatus = playStatus;
        for (int i = 0; i < playStatus.length(); i++) {

            if (playStatus.charAt(i) == 'f') {
                finished = false;
                break;
            }
        }

        if (playStatus.contains("p") && !playStatus.contains("f")) {
            finished = true;
        }

        confirmCount++; // only sends the command after everyone confirms end stroke.
        if (confirmCount == getPlayers().size()) {
            confirmCount = 0;
            if (finished) {
                nextTrack();
            } else {
                writeAll(new Packet(PacketType.DATA, Tools.tabularize("game", "startturn", getNextPlayer(playStatus))));
            }
        }

    }

    protected int getNextPlayer(String s) {
        strokeCounter++;
        int player = strokeCounter % getPlayers().size();
        if (s.charAt(player) == 't') {  // if this player has already finihed
            getNextPlayer(s);
        } else { // if player has not finished
            strokesThisTrack++;
        }

        return playersNumber.get(strokeCounter % getPlayers().size());
    }


    protected void updateStats() {

        int players = 0;
        int strokes = 0;
        for (Player p : getPlayers()) {
            if (!p.hasSkipped()) {
                players++;
                strokes += playerStrokes[getPlayerId(p)];
            }
        }
        if (players > 0) { // if someone didnt skip, update stats.
            TrackManager.addStrokes(tracks.get(currentTrack), players, strokes);
            checkRecord();
        }

        TrackManager.save(tracks.get(currentTrack));

    }

    protected boolean checkRecord() {
        if (getLeadingPlayer().hasSkipped()) {
            return false;
        }

        Track track = tracks.get(currentTrack);
        if ("".equals(track.getFirstBestPlayer()) || track.getBestPar() > getLeadingPar()) { // first ever record OR beat previous
            TrackManager.updateStats(track, getLeadingPlayer(), getLeadingPar(), true);
            return true;
        } else if (track.getBestPar() == getLeadingPar()) { // matched par, latest
            TrackManager.updateStats(track, getLeadingPlayer(), getLeadingPar(), false);
            return true;
        }
        return false;
    }

    protected Player getLeadingPlayer() {
        return playerForId(getLeadingPlayerId());
    }

    protected int getLeadingPlayerId() {
        int minValue = playerStrokes[0];
        int id = 0;
        for (int i = 1; i < playerStrokes.length; i++) {
            if (playerStrokes[i] < minValue && !playerForId(id).hasSkipped()) {
                minValue = playerStrokes[i];
                id = i;
            }
        }
        return id;
    }

    protected int getLeadingPar() {
        int minValue = playerStrokes[0];
        for (int i = 1; i < playerStrokes.length; i++) {
            if (playerStrokes[i] < minValue) {
                minValue = playerStrokes[i];
            }
        }
        return minValue;
    }

    protected void nextTrack() {
        updateStats();
        strokesThisTrack = 0;
        strokeCounter = 0;
        currentTrack++;
        if (currentTrack < tracks.size()) { // there is a next track
            Track t = tracks.get(currentTrack);
            StringBuilder buff = new StringBuilder();
            for (int i = 0; i < getPlayers().size(); i++) {
                playerStrokes[i] = 0; // todo proper id's
                getPlayers().get(i).setSkipped(false);
                buff.append("t");
            }
            playStatus = buff.toString().replace("t", "f");
            writeAll(new Packet(PacketType.DATA, Tools.tabularize("game", "resetvoteskip")));
            writeAll(new Packet(PacketType.DATA, Tools.tabularize("game", "starttrack", buff.toString(), gameId, t)));
            writeAll(new Packet(PacketType.DATA, Tools.tabularize("game", "startturn", getFirstPlayer())));
        } else {
            endGame();
        }
    }

    public int getFirstPlayer() {
        return (strokeCounter += currentTrack % playerCount());
    }


    public void voteSkip(Player p) {
        p.setSkipped(true);
        writeExcluding(p, new Packet(PacketType.DATA, Tools.tabularize("game", "voteskip", getPlayerId(p))));
        for (Player player : getPlayers()) {
            if (!player.hasSkipped() && playStatus.charAt(getPlayerId(player)) == 'f') {
                return;
            }
        }

        StringBuilder buff = new StringBuilder();
        boolean needsChange = false;
        int numberOfSkippers = 0;
        for (Player player : getPlayers()) {
            if (player.hasSkipped() && playStatus.charAt(getPlayerId(player)) == 'f') {
                needsChange = true;
                numberOfSkippers++;
                playerStrokes[getPlayerId(player)] = maxStrokes + 1;
            }
            buff.append(playerStrokes[getPlayerId(player)] + "\t");
        }

        if (needsChange && playerCount() > 1 && numberOfSkippers < playerCount()) {
            writeAll(new Packet(PacketType.DATA, Tools.tabularize("game", "changescore", currentTrack, buff.toString())));
        }

        nextTrack();
    }

    public void beginStroke(Player p, String mouseCoords) {
        //todo: anti cheat mechanisms!
        writeExcluding(p, new Packet(PacketType.DATA, Tools.tabularize("game", "beginstroke", getPlayerId(p), mouseCoords)));

    }

    public String getGameString() {
        return Tools.tabularize(gameId, name, passworded ? "t" : "f", perms,
                numPlayers, -1, tracks.size(), tracksType, maxStrokes, strokeTimeout,
                waterEvent, collision, trackScoring, trackScoringEnd, getPlayers().size());
    }

}
