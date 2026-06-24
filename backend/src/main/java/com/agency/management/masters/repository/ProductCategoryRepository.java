package com.agency.management.masters.repository;

import com.agency.management.masters.dto.response.ProductCategoryByIdResponseDto;
import com.agency.management.masters.dto.response.ProductCategoryResponseList;
import com.agency.management.masters.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    @Query(value = "select * from fn_get_product_category_by_id(?1)", nativeQuery = true)
    ProductCategoryByIdResponseDto getProductCategoryById(Long productCategoryId);

    @Query(value="SELECT * FROM fn_get_product_category_list(?1,?2,?3,?4)", nativeQuery = true)
    List<ProductCategoryResponseList> getProductCategoryList(Long id, String searchString, Integer page, Integer size);

    @Query(value="SELECT * FROM fn_get_product_category_list_count(?1,?2)", nativeQuery = true)
    Long getProductCategoryListCount(Long id, String searchString);
}
