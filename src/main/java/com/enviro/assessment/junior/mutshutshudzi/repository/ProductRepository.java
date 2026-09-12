package com.enviro.assessment.junior.mutshutshudzi.repository;

import com.enviro.assessment.junior.mutshutshudzi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
