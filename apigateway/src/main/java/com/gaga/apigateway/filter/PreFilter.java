package com.gaga.apigateway.filter;

import com.gaga.apigateway.utils.JWTUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class PreFilter extends ZuulFilter {
    @Autowired
    JWTUtils jwtUtils;

    public PreFilter() {
    }

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
        //JWTUtils jwtUtils = new JWTUtils();

        if(authorizationHeader == null) return null;

        String message = jwtUtils.validateToken(authorizationHeader);
        log.info("message : " + message);

        if(message != null) {
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            ctx.setResponseBody(message);
        } else {
            String email = jwtUtils.decodeTokenToEmail(authorizationHeader);
            ctx.addZuulRequestHeader("x-forward-email", email);
        }

        return null;
    }
}
