package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserFavoritesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a {@code user_favorites}; las consultas "detalladas" hacen un solo barrido con receta e
 * ingredientes para no multiplicar idas a la base al expandir el payload.
 */
@Repository
public interface UserFavoritesJpaRepository extends JpaRepository<UserFavoritesEntity, String> {

    /**
     * Un favorito y su receta con filas de ingrediente (y producto) ya cargados en la sesión de Hibernate.
     */
    @Query("""
            SELECT DISTINCT uf FROM UserFavoritesEntity uf
            LEFT JOIN FETCH uf.recipe r
            LEFT JOIN FETCH r.ingredients i
            LEFT JOIN FETCH i.product
            WHERE uf.id = :id
            """)
    Optional<UserFavoritesEntity> findDetailedById(@Param("id") String id);

    /** Vista admin de todos los favoritos con receta completa en la misma consulta. */
    @Query("""
            SELECT DISTINCT uf FROM UserFavoritesEntity uf
            LEFT JOIN FETCH uf.recipe r
            LEFT JOIN FETCH r.ingredients i
            LEFT JOIN FETCH i.product
            """)
    List<UserFavoritesEntity> findAllDetailed();

    /** Igual que la anterior pero filtrando por usuario (endpoint /me). */
    @Query("""
            SELECT DISTINCT uf FROM UserFavoritesEntity uf
            LEFT JOIN FETCH uf.recipe r
            LEFT JOIN FETCH r.ingredients i
            LEFT JOIN FETCH i.product
            WHERE uf.user.id = :userId
            """)
    List<UserFavoritesEntity> findDetailedByUserId(@Param("userId") String userId);
}
