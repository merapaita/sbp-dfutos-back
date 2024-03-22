//package com.rosist.difunto.modelSbp;
//
//import java.io.Serializable;
//import java.util.HashSet;
//import java.util.Set;
// 
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.JoinTable;
//import jakarta.persistence.ManyToMany;
//import jakarta.persistence.Table;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
// 
////import org.hibernate.validator.constraints.NotEmpty;
// 
//@Getter
//@Setter
//@ToString
//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
//@Entity
//@Table(name="app_user")
//public class User implements Serializable{
// 
//	@EqualsAndHashCode.Include
//    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
//    private Integer id;
// 
//	@EqualsAndHashCode.Include
////    @NotEmpty
//    @Column(name="sso_id", unique=true, nullable=false)
//    private String ssoId;
//     
////    @NotEmpty
//    @Column(name="password", nullable=false)
//    private String password;
//         
////    @NotEmpty
//    @Column(name="first_name", nullable=false)
//    private String firstName;
// 
////    @NotEmpty
//    @Column(name="last_name", nullable=false)
//    private String lastName;
// 
////    @NotEmpty
//    @Column(name="email", nullable=false)
//    private String email;
// 
////    @NotEmpty
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "app_user_user_profile", 
//             joinColumns = { @JoinColumn(name = "user_id") }, 
//             inverseJoinColumns = { @JoinColumn(name = "user_profile_id") })
//    private Set<UserProfile> userProfiles = new HashSet<UserProfile>();
//}