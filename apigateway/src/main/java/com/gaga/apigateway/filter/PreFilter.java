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

        jwtUtils.validateToken(authorizationHeader);

        String email = jwtUtils.decodeTokenToEmail(authorizationHeader);
        String nickname = jwtUtils.decodeTokenToNickName(authorizationHeader);
        ctx.addZuulRequestHeader("x-forward-email", email);
        ctx.addZuulRequestHeader("x-forward-nickname", nickname);

        /*if(message != null) {
            ctx.setSendZuulResponse(false);
        } else {
            String email = jwtUtils.decodeTokenToEmail(authorizationHeader);
            String nickname = jwtUtils.decodeTokenToNickName(authorizationHeader);
            ctx.addZuulRequestHeader("x-forward-email", email);
            ctx.addZuulRequestHeader("x-forward-nickname", nickname);
        }*/

        return null;
    }
}
