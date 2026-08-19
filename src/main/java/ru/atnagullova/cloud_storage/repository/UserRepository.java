package ru.atnagullova.cloud_storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.atnagullova.cloud_storage.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
