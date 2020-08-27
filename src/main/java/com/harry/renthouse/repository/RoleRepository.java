package com.harry.renthouse.repository;

import com.harry.renthouse.entity.Role;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 角色dao
 * @author Harry Xu
 * @date 2020/5/8 14:33
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findRolesByUserId(Long userId);
}
