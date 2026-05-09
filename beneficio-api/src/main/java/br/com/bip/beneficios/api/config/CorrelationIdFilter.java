package br.com.bip.beneficios.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationIdFilter.class);

	public static final String HEADER_NAME = "X-Correlation-Id";
	public static final String MDC_KEY = "correlationId";
	public static final String ATTRIBUTE_NAME = CorrelationIdFilter.class.getName() + ".correlationId";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String correlationId = resolveCorrelationId(request);
		MDC.put(MDC_KEY, correlationId);
		request.setAttribute(ATTRIBUTE_NAME, correlationId);
		response.setHeader(HEADER_NAME, correlationId);
		try {
			LOGGER.info("HTTP request received method={} uri={}", request.getMethod(), request.getRequestURI());
			filterChain.doFilter(request, response);
		} finally {
			MDC.remove(MDC_KEY);
		}
	}

	private String resolveCorrelationId(HttpServletRequest request) {
		String headerValue = request.getHeader(HEADER_NAME);
		if (StringUtils.hasText(headerValue)) {
			return headerValue.trim();
		}
		return UUID.randomUUID().toString();
	}
}
