//package com.treasurehunt.treasurehunt.entity;
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonDeserialize(builder = User.Builder.class)
//
//public class User {
//    @JsonProperty("user_id")
//    private final String userId;
//
//    @JsonProperty("password")
//    private String password;
//
//    @JsonProperty("first_name")
//    private final String firstName;
//
//    @JsonProperty("last_name")
//    private final String lastName;
//
//    @JsonProperty("email")
//    private final String email;
//
//    @JsonProperty("address")
//    private final String address;
//
//
//    private User(Builder builder) {
//        this.userId = builder.userId;
//        this.password = builder.password;
//        this.firstName = builder.firstName;
//        this.lastName = builder.lastName;
//        this.email = builder.email;
//        this.address = builder.address;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public User setPassword(String password) {
//        this.password = password;
//        return this;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    public static class Builder {
//        @JsonProperty("user_id")
//        private String userId;
//
//        @JsonProperty("password")
//        private String password;
//
//        @JsonProperty("first_name")
//        private String firstName;
//
//        @JsonProperty("last_name")
//        private String lastName;
//
//        @JsonProperty("email")
//        private String email;
//
//        @JsonProperty("address")
//        private String address;
//
//        public Builder userId(String userId) {
//            this.userId = userId;
//            return this;
//        }
//
//        public Builder password(String password) {
//            this.password = password;
//            return this;
//        }
//
//        public Builder firstName(String firstName) {
//            this.firstName = firstName;
//            return this;
//        }
//
//        public Builder lastName(String lastName) {
//            this.lastName = lastName;
//            return this;
//        }
//
//        public User build() {
//            return new User(this);
//        }
//
//        public Builder email(String email) {
//            this.email = email;
//            return this;
//        }
//
//        public Builder address(String address) {
//            this.address = address;
//            return this;
//        }
//
//    }
//}
