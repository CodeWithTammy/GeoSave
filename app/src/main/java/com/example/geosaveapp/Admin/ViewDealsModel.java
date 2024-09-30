package com.example.geosaveapp.Admin;

import java.util.Date;

public class ViewDealsModel {

        private String name;
        private String description;
        private String profilePic;
        private Date timestamp;

        public ViewDealsModel() {
            // Default constructor required for calls to DataSnapshot.getValue(ViewDealsModel.class)
        }

        public ViewDealsModel(String name, String description, String profilePic, Date timestamp) {
            this.name = name;
            this.description = description;
            this.profilePic = profilePic;
            this.timestamp = timestamp;
        }

        public String getname() {
            return name;
        }

        public void setname(String name) {
            this.name = name;
        }

        public String getdescription() {
            return description;
        }

        public void setdescription(String description) {
            this.description = description;
        }

        public String getprofilePic() {
            return profilePic;
        }

        public void setprofilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public Date gettimestamp() {
            return timestamp;
        }

        public void settimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }
    }


