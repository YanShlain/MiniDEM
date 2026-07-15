package com.yanshlain.minidem.ingest.config;

import com.yanshlain.minidem.ingest.domain.Site;
import com.yanshlain.minidem.ingest.repo.SiteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds a fixed set of sites on startup, if the sites table is empty.
 *
 * @Component makes this a Spring-managed bean, same as any other stereotype annotation —
 * it just gets picked up by classpath scanning like everything else.
 *
 * Implementing CommandLineRunner is Spring Boot's hook for "run this once, after the
 * application context is fully wired, before the app starts serving traffic." Spring
 * finds every CommandLineRunner bean and calls run() on each at startup automatically;
 * there's no explicit call site anywhere in our code. Closest analog: a .NET
 * IHostedService's StartAsync, or manually calling an init function at the top of Go's
 * main() — except here Spring discovers and invokes it for you.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final SiteRepository siteRepository;

    // Constructor injection: Spring sees this constructor needs a SiteRepository, finds
    // (or creates) the one bean of that type in the application context, and passes it
    // in automatically when it builds this DataSeeder. No @Autowired needed on a single
    // constructor — Spring infers it since there's only one to choose from.
    public DataSeeder(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @Override
    public void run(String... args) {
        // Guard against re-seeding every restart — ddl-auto=update won't drop existing
        // data, so without this check we'd get duplicate rows (or a unique constraint
        // violation on `code`) every time the app boots against a non-empty database.
        if (siteRepository.count() > 0) {
            return;
        }

        List.of(
                new Site("tel-aviv-office", "Tel Aviv Office"),
                new Site("berlin-office", "Berlin Office"),
                new Site("nyc-branch", "NYC Branch"),
                new Site("singapore-pop", "Singapore PoP"),
                new Site("aws-us-east-1", "AWS us-east-1"),
                new Site("aws-eu-west-1", "AWS eu-west-1")
        ).forEach(siteRepository::save);
    }
}
