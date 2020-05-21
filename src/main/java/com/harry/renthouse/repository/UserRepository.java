package com.harry.renthouse.repository;

import com.harry.renthouse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 用户dao
 * @author Harry Xu
 * @date 2020/5/7 16:09
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByName(String name);

    @Modifying
    @Query("update User as user set user.avatar = :avatar where house.id = :id")
    void updateAvatar(Long id, String avatar);
}
