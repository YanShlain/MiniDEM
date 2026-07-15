package com.yanshlain.minidem.ingest.repo;

import com.yanshlain.minidem.ingest.domain.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Extending JpaRepository<Site, Long> gives us save(), findById(), findAll(), count(),
 * delete(), etc. for free — no implementation needed, Spring generates one at startup.
 *
 * findByCode below is a "derived query": Spring Data parses the method NAME itself
 * ("find by Code") and generates the SQL ("SELECT * FROM sites WHERE code = ?") from
 * it — no query string, no annotation required. This only works because it follows a
 * fairly strict naming convention (findBy<PropertyName>); anything more complex than
 * simple field matching usually needs an explicit @Query instead.
 */
public interface SiteRepository extends JpaRepository<Site, Long> {

    Optional<Site> findByCode(String code);
}
