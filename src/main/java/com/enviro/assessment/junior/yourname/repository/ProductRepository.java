package com.enviro.assessment.junior.yourname.repository;

import com.enviro.assessment.junior.yourname.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
