package me.chan.springbootdeveloper.service;

import java.security.Principal;
import java.util.List;

import lombok.RequiredArgsConstructor;
import me.chan.springbootdeveloper.domain.Article;
import me.chan.springbootdeveloper.dto.AddArticleRequest;
import me.chan.springbootdeveloper.dto.UpdateArticleRequest;
import me.chan.springbootdeveloper.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 키워드나 @NotNull 이 붙은 필드로 생성자를 만들어 줍니다 .
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request,String userName) {
        return blogRepository.save(request.toEntity(userName));
    }


    public List<Article> findAll(){
        return  blogRepository.findAll();
    }

    public Article findById(long id){
        return  blogRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException(("not found: "+id) ));
    }

    public void delete(long id){
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
        authorizeArticleAuthor(article);


        blogRepository.delete(article);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request){

        Article article = blogRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("not found : "+id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(),request.getContent());

        return article;
    }
    // 게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(Article article){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!article.getAuthor().equals(userName)){
            throw new IllegalArgumentException("not authorized");
        }


    }



}
