package com.gaga.apigateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ErrorFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().getThrowable() != null;
    }

    @Override
    public Object run() {
        Throwable throwable = RequestContext.getCurrentContext().getThrowable();
        log.error("Exception was thrown in filters: ", throwable);

        final RequestContext ctx = RequestContext.getCurrentContext();
        log.info("request Path : " + ctx.getRequest().getPathInfo() + "\n error status code : " + ctx.getResponseStatusCode());

        if(ctx.getResponseStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            ZuulException zuulException = new ZuulException(throwable.toString(), HttpStatus.BAD_REQUEST.value(), throwable.getMessage());
            throw new ZuulRuntimeException(zuulException);
        }

        return null;
    }
}
