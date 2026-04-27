package org.example.proektnupraktikum.Repository;

import org.example.proektnupraktikum.Entity.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    @Query(value = """
            SELECT * FROM vacancy
            WHERE to_tsvector('russian', title || ' ' || coalesce(description, ''))
               @@ to_tsquery('russian', :q)
            ORDER BY ts_rank(
                to_tsvector('russian', title || ' ' || coalesce(description, '')),
                to_tsquery('russian', :q)
            ) DESC
            """, nativeQuery = true)
    List<Vacancy> searchByText(@Param("q") String q);
}