package kusitms.gallae.repository.favorite;

import kusitms.gallae.domain.Favorite;
import kusitms.gallae.domain.Program;
import kusitms.gallae.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserAndProgram(User user, Program program);

    Optional<Favorite> findByUserAndProgram(User user, Program program);

    List<Favorite> findAllByUser(User user);

    void deleteAllByProgram(Program program);
}
