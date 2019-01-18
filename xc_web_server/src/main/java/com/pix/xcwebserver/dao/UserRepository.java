package com.pix.xcwebserver.dao;

import com.pix.xcwebserver.bean.XCUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<XCUser,Integer> {
}
