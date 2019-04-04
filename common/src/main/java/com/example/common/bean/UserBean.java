package com.example.common.bean;

/**
 * Created by Liszt on 2018/9/22.
 */

public class UserBean {

  private String userId;
  private String userName;
  private String userRemarkName;
  private int sex;
  private long time;

  public static Builder newBuilder() {
    return new Builder();
  }

 public static class Builder {

    private UserBean userBean = new UserBean();

    public Builder setUserId(String value) {
      userBean.userId = value;
      return this;
    }

    public Builder setUserName(String value) {
      userBean.userName = value;
      return this;
    }

    public Builder setUserRemarkName(String value) {
      userBean.userRemarkName = value;
      return this;
    }

    public Builder setSex(int value) {
      userBean.sex = value;
      return this;
    }

    public Builder setTime(long value) {
      userBean.time = value;
      return this;
    }

    public UserBean builder() {
      return userBean;
    }
  }


}
