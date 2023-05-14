package entelect.training.incubator.spring.authentication.repository;

import entelect.training.incubator.spring.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    public User findByUsernameAndPassword(String username, String password);
}
