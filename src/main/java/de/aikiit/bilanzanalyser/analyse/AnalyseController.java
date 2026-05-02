package de.aikiit.bilanzanalyser.analyse;

import de.aikiit.bilanzanalyser.entity.database.CategoryEntity;
import de.aikiit.bilanzanalyser.entity.database.repository.CategoryRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@CrossOrigin // allow frontend access
public final class AnalyseController {
    private final CategoryRepository categoryRepository;

    public AnalyseController(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/analyse")
    public ModelAndView analyse() {
        ModelAndView mav = new ModelAndView("analyse");
        // mav.addObject("worksheets", sourceService.getSources());
        return mav;
    }

    @RequestMapping("/api/categories")
    public List<CategoryEntity> getCategories() {
        return categoryRepository.findAll();
    }


}
