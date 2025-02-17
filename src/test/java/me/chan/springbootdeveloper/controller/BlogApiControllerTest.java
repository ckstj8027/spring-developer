package me.chan.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import me.chan.springbootdeveloper.domain.Article;
import me.chan.springbootdeveloper.domain.User;
import me.chan.springbootdeveloper.dto.AddArticleRequest;
import me.chan.springbootdeveloper.dto.UpdateArticleRequest;
import me.chan.springbootdeveloper.repository.BlogRepository;
import me.chan.springbootdeveloper.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;


    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc= MockMvcBuilders.webAppContextSetup(context).build();
        blogRepository.deleteAll();

    }
    @BeforeEach
    void setSecurity(){
        userRepository.deleteAll();
        userRepository.deleteAll();
        user=userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("text")
                .build());

        SecurityContext context = SecurityContextHolder.getContext();

        context.setAuthentication(new UsernamePasswordAuthenticationToken(user,user.getPassword() ,user.getAuthorities()));


    }





    @DisplayName("addArticle: 블록그 글 추가에 성공한다.")
    @Test
    public void addArticle() throws Exception {
        final String url="/api/articles";
        final String title="title";
        final String content="content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);
        // 객체를 json 으로 직렬화
        final String requestBody=objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);  // Principal의 mock 객체를 생성
        Mockito.when(principal.getName()).thenReturn("username");  // getName()이 호출되면 "username"을 반환

        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                .content(requestBody));


        // {
        //    "title":  "제목",
        //    "content":  "내용"
        //}  마치 포스트맨에 raw 의 json 형태로 저 url 에 post 요청한 것이 됨



        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        Assertions.assertThat(articles.size()).isEqualTo(1);

        Assertions.assertThat(articles.get(0).getTitle()).isEqualTo(title);

        Assertions.assertThat(articles.get(0).getContent()).isEqualTo(content);


    }

    @DisplayName("findAllArticles: 블록그 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception {

        final String url="/api/articles";
        Article savedArticle = createDefaultArticle();

        ResultActions resultActions = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()));


    }
    @DisplayName("findArticle: 블록그 단건 조회에 성공한다.")
    @Test
    public void findArticle() throws Exception {
        final String url="/api/articles/{id}";
        final String title="title";
        final String content="content";

        Article savedArticle =  createDefaultArticle();

        ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(savedArticle.getTitle()))
                .andExpect(jsonPath("$.content").value(savedArticle.getContent()));

    }

    @DisplayName("deleteArticle: 블록그 글 삭제에 성공한다.")
    @Test
    public void deleteArticle() throws Exception {

        final String url="/api/articles/{id}";
        final String title="title";
        final String content="content";

        Article savedArticle =  createDefaultArticle();

        mockMvc.perform(delete(url,savedArticle.getId()))
                .andExpect(status().isOk());

        List<Article> articles = blogRepository.findAll();

        Assertions.assertThat(articles).isEmpty();

    }

    @DisplayName("deleteArticle: 블록그 글 수정에 성공한다.")
    @Test
    public void updateArticle() throws Exception {
        final String url="/api/articles/{id}";
        final String title="title";
        final String content="content";

        Article savedArticle =  createDefaultArticle();
        UpdateArticleRequest request = new UpdateArticleRequest("new t", "new c");
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());

        Article article = blogRepository.findById(savedArticle.getId()).get();

        Assertions.assertThat(article.getTitle()).isEqualTo("new t");
        Assertions.assertThat(article.getContent()).isEqualTo("new c");

    }


    private Article createDefaultArticle(){

        final String title="title";
        final String content="content";



       return blogRepository.save(Article.builder()
                .title(title)
                       .author(user.getUsername())
                .content(content)
                .build());



    }

}