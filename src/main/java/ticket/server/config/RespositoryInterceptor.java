package ticket.server.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

@Aspect
public class RespositoryInterceptor {

	private final static Logger logger = LoggerFactory.getLogger(RespositoryInterceptor.class);

	@Around("execution(* ticket.server.repository..*Repository.*(..))")
	public Object logQueryTimes(ProceedingJoinPoint pjp) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Object retVal = pjp.proceed();
		stopWatch.stop();
		long millis = stopWatch.getTotalTimeMillis();
		String str = pjp.getTarget().toString();
		logger.info(str.substring(str.lastIndexOf(".") + 1, str.lastIndexOf("@")) + " - " + pjp.getSignature().getName()
				+ ": " + millis + "ms");
		return retVal;
	}
}