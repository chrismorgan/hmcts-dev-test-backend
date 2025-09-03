package uk.gov.hmcts.reform.dev.repositories;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.dev.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    Optional<Task> findBySystemId(UUID systemId);

}
