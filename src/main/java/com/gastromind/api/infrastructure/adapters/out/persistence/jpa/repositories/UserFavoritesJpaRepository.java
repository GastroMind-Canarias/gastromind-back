package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserFavoritesEntity;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoritesJpaRepository extends JpaRepository<UserFavoritesEntity, String> {

    @Query("SELECT uf FROM UserFavoritesEntity uf WHERE uf.user.id = :userId")
    List<UserFavoritesEntity> findByUserId(@Param("userId") String userId);

    @Query("SELECT uf FROM UserFavoritesEntity uf WHERE uf.user.id = :userId AND uf.recipe.id = :recipeId")
    Optional<UserFavoritesEntity> findByUserIdAndRecipeId(@Param("userId") String userId,
            @Param("recipeId") String recipeId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserFavoritesEntity uf WHERE uf.user.id = :userId AND uf.recipe.id = :recipeId")
    void deleteByUserIdAndRecipeId(@Param("userId") String userId, @Param("recipeId") String recipeId);
}
