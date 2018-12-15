package com.eric.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

/**
 * Created by eric on 2017/9/21.
 */
@ControllerAdvice
public class ControllerConfig {

    @Autowired
    ResourceUrlProvider resourceUrlProvider;

    @ModelAttribute("urls")
    public ResourceUrlProvider urls() {
        return this.resourceUrlProvider;
    }
}
