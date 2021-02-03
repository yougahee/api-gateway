package com.gaga.apigateway.filter;

import com.gaga.apigateway.dto.UserDTO;
import com.gaga.apigateway.utils.JWTUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class PreFilter extends ZuulFilter {

    private static final Gson gson = new GsonBuilder().create();

    @Autowired
    JWTUtils jwtUtils;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @SneakyThrows
    @Override
    public Object run() {
        log.info("=====Pre Filter Start=====");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String authorizationHeader = request.getHeader("token");
        log.info(String.format("\n" +
                        "[request] %s \n " +
                        "URL %s \n",
                request.getMethod(),
                request.getRequestURL().toString()
        ));
        log.info("authorizationHeader의 값 : " + authorizationHeader);

        if(authorizationHeader == null) return null;

        if(jwtUtils.checkToken(authorizationHeader)) {
            UserDTO userDTO = jwtUtils.decodeJWT(authorizationHeader);
            ctx.addZuulRequestHeader("x-forward-email", userDTO.getEmail());
            ctx.addZuulRequestHeader("x-forward-nickname", URLEncoder.encode(userDTO.getNickname(), StandardCharsets.UTF_8));
            ctx.addZuulRequestHeader("x-forward-userIdx", userDTO.getUserIdx());
            log.info("nickname : " + userDTO.getNickname());
        } else {
            JsonObject response = new JsonObject();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            response.addProperty("timestamp", LocalDateTime.now().format(formatter));
            response.addProperty("status", 401);
            response.addProperty("message", "token Error");
            response.addProperty("path", request.getRequestURI());

            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            ctx.setResponseBody(response.toString());
        }

        return null;
    }
}
