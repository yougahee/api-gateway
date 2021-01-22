package com.gaga.apigateway.filter;

import com.gaga.apigateway.utils.JWTUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
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

    @Override
    public Object run() {
        log.info("=====Pre Filter Start=====");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String authorizationHeader = request.getHeader("token");

        if(authorizationHeader == null) return null;

        String message = jwtUtils.validateToken(authorizationHeader);
        log.info("message : " + message);

        if(message != null) {
            JsonObject response = new JsonObject();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            response.addProperty("timestamp", LocalDateTime.now().format(formatter));
            response.addProperty("message", message);
            response.addProperty("path", request.getRequestURI());

            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            ctx.setResponseBody(message);
        } else {
            String email = jwtUtils.decodeTokenToEmail(authorizationHeader);
            String nickname = jwtUtils.decodeTokenToNickName(authorizationHeader);
            ctx.addZuulRequestHeader("x-forward-email", email);
            ctx.addZuulRequestHeader("x-forward-nickname", nickname);
        }

        return null;
    }
}
