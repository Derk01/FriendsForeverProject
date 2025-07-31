public class Friend {
    private String friendId;
    private String friendName;

    public Friend() {} // Needed for Firestore

    public Friend(String friendId, String friendName) {
        this.friendId = friendId;
        this.friendName = friendName;
    }

    public String getFriendId() {
        return friendId;
    }

    public String getFriendName() {
        return friendName;
    }
}
