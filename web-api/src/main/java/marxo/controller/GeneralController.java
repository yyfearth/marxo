package marxo.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/")
public class GeneralController {
    final Logger logger = LoggerFactory.getLogger(GeneralController.class);
    @Autowired
    ApplicationContext applicationContext;

    @PostConstruct
    void report() {
        logger.debug(GeneralController.class.getSimpleName() + " started");

        Boolean isDebug = applicationContext.getBean("isDebug", Boolean.class);
        isDebug = (isDebug == null) ? false : isDebug;

        if (isDebug) {
            // Prevent the JVM to prompt OutOfMemory while IntelliJ redeploys the app. (fuck dat JVM)
            System.gc();
        }
    }

    @RequestMapping
    @ResponseBody
    public Message get(HttpServletRequest request) {
        return new Message(request);
    }

    @RequestMapping("/test{:s?}")
    @ResponseBody
    public List<Double> getWorkflow() {
        List<Double> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add(Math.random());
        }

        return list;
    }

    @JsonSerialize
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    class Message {
        String ip;
        String host;
        int port;
        Map<String, String> headers;

        public Message(HttpServletRequest request) {
            ip = request.getRemoteAddr();
            host = request.getRemoteHost();
            port = request.getRemotePort();

            headers = new HashMap<>();
            List<String> headerNames = Collections.list(request.getHeaderNames());
            for (String headerName : headerNames) {
                headers.put(headerName, request.getHeader(headerName));
            }
        }
    }
}
