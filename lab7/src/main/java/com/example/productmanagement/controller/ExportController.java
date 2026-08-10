package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private ProductService productService;

    @GetMapping("/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {

        // Set response (force download)
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=products.xlsx");

        // Get product list
        List<Product> products = productService.getAllProducts();

        // Create workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        // Header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        // Create header row
        Row header = sheet.createRow(0);

        String[] columns = {"ID", "Product Code", "Name", "Price", "Quantity", "Category", "Created At"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // Write product data
        int rowNum = 1;
        for (Product p : products) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getProductCode());
            row.createCell(2).setCellValue(p.getName());

            // Price (BigDecimal to double)
            BigDecimal price = p.getPrice();
            row.createCell(3).setCellValue(price != null ? price.doubleValue() : 0);

            row.createCell(4).setCellValue(p.getQuantity());
            row.createCell(5).setCellValue(p.getCategory());

            if (p.getCreatedAt() != null) {
                row.createCell(6).setCellValue(p.getCreatedAt().toString());
            } else {
                row.createCell(6).setCellValue("N/A");
            }
        }

        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to browser
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}

