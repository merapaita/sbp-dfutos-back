//package com.rosist.difunto.modelSbp;
//
//import java.io.Serializable;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//
//@Getter
//@Setter
//@ToString
//@EqualsAndHashCode
//@Entity
//@Table(name="user_profile")
//public class UserProfile implements Serializable{
//
//    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
//    private Integer id;
//
//    @Column(name="type", length=15, unique=true, nullable=false)
//    private String type = UserProfileType.CST_USER.getUserProfileType();
//
//}