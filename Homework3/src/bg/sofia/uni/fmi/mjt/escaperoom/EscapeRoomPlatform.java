package bg.sofia.uni.fmi.mjt.escaperoom;

import bg.sofia.uni.fmi.mjt.escaperoom.exception.PlatformCapacityExceededException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.RoomAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.RoomNotFoundException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.TeamNotFoundException;
import bg.sofia.uni.fmi.mjt.escaperoom.room.EscapeRoom;
import bg.sofia.uni.fmi.mjt.escaperoom.room.Review;
import bg.sofia.uni.fmi.mjt.escaperoom.team.Team;

public class EscapeRoomPlatform implements EscapeRoomPortalAPI, EscapeRoomAdminAPI {

    private static final int POINTS_FOR_FAST_ESCAPING = 2; // under 50% of the time
    private static final int POINTS_FOR_MEDIUM_ESCAPING = 1; // under 75% of the time

    private final Team[] teams;
    private final int maxCapacity;
    private int roomsCount;
    private EscapeRoom[] rooms;


    public EscapeRoomPlatform(Team[] teams, int maxCapacity) {
        this.teams = teams;
        this.maxCapacity = maxCapacity;
        roomsCount = 0;
    }

    @Override
    public void addEscapeRoom(EscapeRoom room) throws RoomAlreadyExistsException {
        if (room == null) {
            throw new IllegalArgumentException("The room is null");
        }

        if (roomsCount >= maxCapacity) {
            throw new PlatformCapacityExceededException();
        }

        for (int i = 0; i < roomsCount; i++) {
            if (rooms[i].equals(room)) {
                throw new RoomAlreadyExistsException();
            }
        }

        insertEscapeRoom(room);
    }

    private void insertEscapeRoom(EscapeRoom room) {
        EscapeRoom[] temp = rooms;
        rooms = new EscapeRoom[roomsCount + 1];
        for (int i = 0; i < roomsCount; i++) {
            rooms[i] = temp[i];
        }

        rooms[roomsCount] = room;
        roomsCount++;
    }

    @Override
    public void removeEscapeRoom(String roomName) throws RoomNotFoundException {
        validateString(roomName);

        EscapeRoom roomToDelete = getEscapeRoomByName(roomName);
        deleteEscapeRooms(roomToDelete);
        roomsCount--;
    }

    private void validateString(String str) {
        if (str == null || str.isEmpty() || str.isBlank()) {
            throw new IllegalArgumentException("Invalid string");
        }
    }

    private void deleteEscapeRooms(EscapeRoom toDelete) {
        EscapeRoom[] temp = rooms;
        rooms = new EscapeRoom[roomsCount - 1];

        int roomsCopied = 0;
        for (EscapeRoom room : temp) {
            if (room != toDelete) {
                rooms[roomsCopied] = room;
                roomsCopied++;
            }
        }
    }

    @Override
    public EscapeRoom[] getAllEscapeRooms() {
        return rooms;
    }

    @Override
    public void registerAchievement(String roomName, String teamName, int escapeTime) throws RoomNotFoundException, TeamNotFoundException {
        validateString(roomName);
        validateString(teamName);
        if (escapeTime <= 0) {
            throw new IllegalArgumentException("Escape time must be a positive number");
        }

        EscapeRoom room = getEscapeRoomByName(roomName);

        if (room.getMaxTimeToEscape() < escapeTime) {
            throw new IllegalArgumentException("The team's escape time must be less than the max escape time for the room");
        }

        Team team = findTeamByName(teamName);
        int pointsWon = calculateTeamPoints(escapeTime, room);
        team.updateRating(pointsWon);
    }

    private Team findTeamByName(String teamName) throws TeamNotFoundException {
        if (teams == null) {
            throw new TeamNotFoundException();
        }

        for (Team team : teams) {
            if (team.getName().equals(teamName)) {
                return team;
            }
        }

        throw new TeamNotFoundException();
    }

    private int calculateTeamPoints(int escapeTime, EscapeRoom room) {
        int points = room.getDifficulty().getRank();
        if (escapeTime <= room.getMaxTimeToEscape() / 2) {
            points += POINTS_FOR_FAST_ESCAPING;
        } else if (escapeTime <= room.getMaxTimeToEscape() * 3 / 4) {
            points += POINTS_FOR_MEDIUM_ESCAPING;
        }

        return points;
    }

    @Override
    public EscapeRoom getEscapeRoomByName(String roomName) throws RoomNotFoundException {
        validateString(roomName);

        if (rooms != null) {
            for (EscapeRoom room : rooms) {
                if (room.getName().equals(roomName)) {
                    return room;
                }
            }
        }

        throw new RoomNotFoundException();
    }

    @Override
    public void reviewEscapeRoom(String roomName, Review review) throws RoomNotFoundException {
        EscapeRoom room = getEscapeRoomByName(roomName);
        room.addReview(review);
    }

    @Override
    public Review[] getReviews(String roomName) throws RoomNotFoundException {
        EscapeRoom room = getEscapeRoomByName(roomName);
        return room.getReviews();
    }

    @Override
    public Team getTopTeamByRating() {
        if (teams == null) {
            return null;
        }

        double maxScore = -1;
        int maxIndex = -1;

        for (int i = 0; i < teams.length; i++) {
            if (teams[i].getRating() > maxScore) {
                maxScore = teams[i].getRating();
                maxIndex = i;
            }
        }

        if (maxIndex == -1) {
            return null;
        }

        return teams[maxIndex];
    }
}
