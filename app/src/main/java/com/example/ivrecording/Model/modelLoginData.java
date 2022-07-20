package com.example.ivrecording.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class modelLoginData {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class UserData {

        @SerializedName("id")
        @Expose
        private Integer id;

        @SerializedName("mobile")
        @Expose
        private String mobile;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("department_id")
        @Expose
        private String departmentId;

        @SerializedName("role_id")
        @Expose
        private String roleId;

        @SerializedName("email")
        @Expose
        private String email;

        @SerializedName("email_verified_at")
        @Expose
        private Object emailVerifiedAt;

        @SerializedName("created_at")
        @Expose
        private String createdAt;

        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        @SerializedName("recording_details")
        @Expose
        private String recording_details;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(String departmentId) {
            this.departmentId = departmentId;
        }

        public String getRoleId() {
            return roleId;
        }

        public void setRoleId(String roleId) {
            this.roleId = roleId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Object getEmailVerifiedAt() {
            return emailVerifiedAt;
        }

        public void setEmailVerifiedAt(Object emailVerifiedAt) {
            this.emailVerifiedAt = emailVerifiedAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getRecordingStorePath() {
            return this.recording_details;
        }

        public void setRecordingStorePath(String recording_details) {
            this.recording_details = recording_details;
        }
    }

    public class Data {

        @SerializedName("token")
        @Expose
        private String token;

        @SerializedName("user_data")
        @Expose
        private UserData userData;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public UserData getUserData() {
            return userData;
        }

        public void setUserData(UserData userData) {
            this.userData = userData;
        }
    }
}
