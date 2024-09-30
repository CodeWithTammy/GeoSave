package com.example.geosaveapp.Admin;

import java.util.Date;

public class ViewUserModel {

        private String Name;
        private String UserEmail;
        private String userType;
        private Date signUpDate;
        private String profilePic;
        private String id;

        public ViewUserModel() {
            // Default constructor required for calls to DataSnapshot.getValue(ViewUserModel.class)
        }

        public ViewUserModel(String Name, String UserEmail, String userType, Date signUpDate, String profilePic, String id) {
            this.Name = Name;
            this.UserEmail = UserEmail;
            this.userType = userType;
            this.signUpDate = signUpDate;
            this.profilePic = profilePic;
            this.id = id;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getUserEmail() {
            return UserEmail;
        }

        public void setUserEmail(String UserEmail) {
            this.UserEmail = UserEmail;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public Date getsignUpDate() {
            return signUpDate;
        }

        public void setsignUpDate(Date signUpDate) {
            this.signUpDate = signUpDate;
        }

        public String getprofilePic() {
            return profilePic;
        }

        public void setprofilePic(String profilePic) {
            this.profilePic = profilePic;
        }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}



