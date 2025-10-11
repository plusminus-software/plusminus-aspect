package software.plusminus.aspect;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@AllArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class HttpAspect extends OncePerRequestFilter {

    public static final ThreadLocal<HttpServletRequest> HTTP_REQUEST = new ThreadLocal<>();
    public static final ThreadLocal<HttpServletResponse> HTTP_RESPONSE = new ThreadLocal<>();

    private AspectManager aspectManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) {
        try {
            HTTP_REQUEST.set(request);
            HTTP_RESPONSE.set(response);
            aspectManager.run(() -> filterChain.doFilter(request, response));
        } finally {
            HTTP_REQUEST.remove();
            HTTP_RESPONSE.remove();
        }
    }
}
