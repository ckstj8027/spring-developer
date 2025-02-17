package me.chan.springbootdeveloper.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id",updatable = false)
    private Long id;

    @Column(name="title",nullable = false )
    private String title;

    @Column(name="content",nullable  = false)
    private String content;

    @Column(name="author",nullable = false)
    private String author;

    @Builder // 빌더 패턴으로 객체 생성
    public Article(String author,String title,String content){
        this.author=author;
        this.title=title;
        this.content=content;
    }
    // 빌더 패턴 사용 안할때  new Article("abc","def");
    // 빌더 패턴 사용 Article.builder().title("abd").content("def").build()


//    protected  Article(){
//        // 기본 생성자
//    }
//    public Long getId(){
//        return id;
//    }
//    public String getTitle(){
//        return title;
//    }
//
//    public String getContent(){
//        return content;
//    }

    public void update(String title,String content){
        this.title=title;
        this.content=content;
    }


    @CreatedDate
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name="updated_at")
    private LocalDateTime updatedAt;



}
