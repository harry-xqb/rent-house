package com.harry.renthouse.repository;

import com.harry.renthouse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户dao
 * @author Harry Xu
 * @date 2020/5/7 16:09
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
