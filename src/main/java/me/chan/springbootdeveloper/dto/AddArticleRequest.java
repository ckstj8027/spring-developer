package me.chan.springbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.chan.springbootdeveloper.domain.Article;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class AddArticleRequest {

    private String title;
    private String content;

    public Article toEntity(String author){
        // 이 함수는 빌더 패턴을 이용해 dto 를 엔티티로 만들어주는 메서드입니다.
        // 이 메서드는 추후에 블로그 글을 추가할때 저장할 엔티티로 변환하는 용도로 사용합니다.
        return Article.builder().title(title).content(content).author(author).build();
    }

}
