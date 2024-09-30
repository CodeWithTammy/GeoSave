package com.example.geosaveapp.Components;

import com.google.firebase.Timestamp;

public class storeModel {


        private String profilePic;
        private String Name;


        public storeModel() {
            // Required empty constructor for Firestore
        }

        public storeModel(String profilepic, String Name) {
            this.profilePic = profilepic;
            this.Name = Name;

        }

        public String getprofilepic() {
            return profilePic;
        }

        public void setprofilepic(String profilepic) {

            this.profilePic = profilepic;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }



    }




