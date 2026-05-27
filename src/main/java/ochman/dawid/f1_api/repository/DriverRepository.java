package ochman.dawid.f1_api.repository;

import ochman.dawid.f1_api.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByCarNumber(Integer carNumber);
    long countByTeam(String team);
    long countByTeamAndIdNot(String team, Long id);
}
