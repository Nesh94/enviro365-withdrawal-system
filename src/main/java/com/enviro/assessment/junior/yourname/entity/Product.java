package com.enviro.assessment.junior.yourname.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * A single investment product held by an investor (e.g. a Retirement
 * Annuity or a Unit Trust), each carrying its own balance.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String productType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    // @JsonIgnore stops Jackson from recursing back through Investor.products
    // when serializing a Product to JSON. @ToString.Exclude /
    // @EqualsAndHashCode.Exclude stop Lombok's generated methods from doing
    // the same thing (see the matching note on Investor.products).
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id", nullable = false)
    @JsonIgnore
    private Investor investor;
}
