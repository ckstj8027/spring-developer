package me.chan.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Base64;
//import org.springframework.util.SerializationUtils;
import org.apache.commons.lang3.SerializationUtils;
public class CookieUtil {


    public static void addCookie(HttpServletResponse response,String name,String value,int maxAge){

        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);


    }
    public static void deleteCookie(HttpServletRequest request,HttpServletResponse response,String name){
        Cookie[] cookies = request.getCookies();
        if(cookies==null){
            return;
        }

        for (Cookie cookie : cookies) {
            if(name.equals(cookie.getName())){
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);


            }
        }


    }
    // 객체를 직렬화해 쿠키의 값으로 변환
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize((Serializable) obj));
    }

    // 쿠키를 역직렬화해 객체로 변환
    public static<T> T deserialize(Cookie cookie,Class<T> cls){
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));


    }











}
