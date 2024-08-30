package com.example.bestore2.controllers;

import com.example.bestore2.models.Product;
import com.example.bestore2.services.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductsRepository repo;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping({"", "/"})
    public String showProductList(Model model) {
        List<Product> products = repo.findAll();
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        return "products/create";
    }

    @PostMapping("/create")
    public String createProduct(@ModelAttribute("product") Product product,
                                @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                // Đặt tên file ngẫu nhiên để tránh trùng lặp
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                // Đường dẫn lưu trữ file
                File file = new File(uploadDir + File.separator + fileName);
                // Tạo thư mục nếu chưa tồn tại
                file.getParentFile().mkdirs();
                imageFile.transferTo(file);

                // Lưu đường dẫn vào cơ sở dữ liệu
                product.setImageFilename(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                // Thêm xử lý lỗi nếu cần
            }
        }

        product.setCreatedAt(new Date());
        repo.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
        Product product = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "products/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable("id") int id,
                                @ModelAttribute("product") Product product,
                                @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                // Đặt tên file ngẫu nhiên để tránh trùng lặp
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                // Đường dẫn lưu trữ file
                File file = new File(uploadDir + File.separator + fileName);
                // Tạo thư mục nếu chưa tồn tại
                file.getParentFile().mkdirs();
                imageFile.transferTo(file);

                // Lưu đường dẫn vào cơ sở dữ liệu
                product.setImageFilename(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                // Thêm xử lý lỗi nếu cần
            }
        } else {
            // Giữ lại hình ảnh cũ nếu không tải lên hình ảnh mới
            Product existingProduct = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
            product.setImageFilename(existingProduct.getImageFilename());
        }

        product.setId(id);
        product.setCreatedAt(new Date());
        repo.save(product);
        return "redirect:/products";
    }
}
