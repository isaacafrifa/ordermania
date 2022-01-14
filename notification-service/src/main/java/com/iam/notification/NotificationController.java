package com.iam.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);


    @GetMapping(value = "/notify")
    public String getAll() {
        LOGGER.info("TESTING NOTIFICATION SERVICE");
        return "Endpoint Reached";
    }


}
