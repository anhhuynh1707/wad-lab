package com.example.productmanagement.controller;

import com.example.productmanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String showDashboard(Model model) {

        List<String> categories = productService.getAllCategories();

        // Map for category statistics
        Map<String, Long> categoryStats = new HashMap<>();
        for (String c : categories) {
            categoryStats.put(c, productService.countProductsByCategory(c));
        }

        model.addAttribute("categoryStats", categoryStats);
        model.addAttribute("totalValue", productService.getTotalValue());
        model.addAttribute("avgPrice", productService.getAveragePrice());
        model.addAttribute("lowStock", productService.getLowStockProducts(10));
        model.addAttribute("recentProducts", productService.getRecentProducts());

        return "dashboard";
    }
}