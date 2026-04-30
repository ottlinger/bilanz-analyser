package de.aikiit.bilanzanalyser.analyse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public final class AnalyseController {
    @GetMapping("/analyse")
    public ModelAndView analyse() {
        ModelAndView mav = new ModelAndView("analyse");
        // mav.addObject("worksheets", sourceService.getSources());
        return mav;
    }

}
