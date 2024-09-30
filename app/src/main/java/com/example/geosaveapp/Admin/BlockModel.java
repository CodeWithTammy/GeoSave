package com.example.geosaveapp.Admin;

public class BlockModel {

        private String id;
        private String UserEmail;
        private boolean isBlocked;
        private String name;
        private String profilePic;

        public BlockModel() {
            // Firestore requires a public no-argument constructor
        }
    public BlockModel(String id, String UserEmail, boolean isBlocked, String profilePic) {
        this.id = id;
        this.UserEmail = UserEmail;
        this.isBlocked = isBlocked;
        this.profilePic = profilePic;
    }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserEmail() {
            return UserEmail;
        }

        public void setUserEmail(String UserEmail) {
            this.UserEmail = UserEmail;
        }

        public boolean isBlocked() {
            return isBlocked;
        }

        public void setBlocked(boolean blocked) {
            isBlocked = blocked;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}


