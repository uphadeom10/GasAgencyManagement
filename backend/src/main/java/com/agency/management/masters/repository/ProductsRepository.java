package com.agency.management.masters.repository;

import com.agency.management.masters.dto.response.ProductByIdResponseDto;
import com.agency.management.masters.dto.response.ProductResponseList;
import com.agency.management.masters.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {

    @Query(value ="select * from fn_get_product_by_id(?1)", nativeQuery = true)
    ProductByIdResponseDto getProductById(Long productId);

    @Query(value = "select * from fn_get_product_list(?1,?2,?3,?4)",nativeQuery = true)
    List<ProductResponseList> getProductList(Long id, String searchString, Integer page, Integer size);

    @Query(value = "select * from fn_get_product_count_list(?1,?2)",nativeQuery = true)
    Long getProductCountList(Long id, String searchString);

}



