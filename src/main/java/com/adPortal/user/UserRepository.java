package com.adPortal.user;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	boolean existsByEmail(String walletAddress);

	User findByEmail(String email);

	List<User> findByRole(String string);

	List<User> findByWalletAddress(String walletAddress);

	User getUserByWalletAddress(String walletAddress);

	User getUserRoleById(long userId);


	//String findUserRoleById(long userId);

}
