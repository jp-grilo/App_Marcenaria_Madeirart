package com.madeirart.appMadeirart.modules.orcamento.repository;

import com.madeirart.appMadeirart.modules.orcamento.entity.ItemMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para a entidade ItemMaterial
 */
@Repository
public interface ItemMaterialRepository extends JpaRepository<ItemMaterial, Long> {
}
