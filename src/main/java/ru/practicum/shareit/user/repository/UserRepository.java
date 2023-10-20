package ru.practicum.shareit.user.repository;

<<<<<<< HEAD
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

=======
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectExistException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    public void save(User user) {
        user.setUserId(++id);
        users.put(user.getUserId(), user);
    }

    public void update(User user) {
        for (long index : users.keySet()) {
            if (index != user.getUserId()) {
                if (users.get(index).getEmail().equals(user.getEmail())) {
                    throw new ObjectExistException("This email is already in use.");
                }
            }
        }
        users.replace(user.getUserId(), user);
    }

    public List<User> getAllUsers() {
        List<User> savedUsers = new ArrayList<>();
        for (Long id : users.keySet()) {
            savedUsers.add(users.get(id));
        }
        return savedUsers;
    }

    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public void delete(long userId) {
        users.remove(userId);
    }
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
}
