package com.example.bestore2.services;


import com.example.bestore2.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductsRepository extends JpaRepository<Product, Integer> {


}
