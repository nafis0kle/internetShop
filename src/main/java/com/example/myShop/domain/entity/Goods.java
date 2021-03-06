package com.example.myShop.domain.entity;

import com.example.myShop.domain.exception.LinkedOrdersExistsException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static lombok.AccessLevel.PRIVATE;

/**
 * @author nafis
 * @since 19.12.2021
 */

@Entity
@Setter
@Getter
@Table(name = "goods")
public class Goods extends BaseEntity{
    private String name;
    private BigDecimal price;
    private Long count;
    private String imageUrl;

    @Setter(PRIVATE)
    @OneToMany(mappedBy = "goods",
               orphanRemoval = true,
            cascade = {PERSIST, MERGE, DETACH, REFRESH})
    private List<SelectedProduct> selectedProducts = new ArrayList<>();

    @JoinColumn(name = "producer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Producer producer;

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @PreRemove
    public void beforeDelete(){
        if(!selectedProducts.isEmpty()){
            throw new LinkedOrdersExistsException(this.id, this.getClass().getName());
        }
    }

    public void decCount(Long orderCount){
        count -= orderCount;
    }

    public void incCount(Long orderCount) {
        count += orderCount;
    }
}
