package com.sentimentdiary.demo.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sentimentdiary.demo.auditable.Auditable;
import com.sentimentdiary.demo.diary.Diary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column
    private String pw;

    @Column(length = 20, nullable = false)
    private String nickName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    boolean google;


    @JsonIgnore
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    private List<Diary> diaries = new ArrayList<>();


    public void addDiary(Diary diary) {
        diaries.add(diary);
        if(diary.getMember() != this) {
            diary.setMember(this);
        }
    }


    public Member(long memberId, String pw, String nickName, String email, String phoneNumber) {
        this.memberId = memberId;
        this.pw = pw;
        this.nickName = nickName;
        this.email = email;
    }

    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();


    public Member update(String email, String name){
        this.email = email;
        this.nickName = name;

        return this;
    }

}