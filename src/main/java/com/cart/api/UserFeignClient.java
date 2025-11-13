package com.cart.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.cart.dto.AddressDTO;
import com.cart.dto.UserDTO;

@FeignClient(name = "UserService", url = "http://localhost:9999/")
public interface UserFeignClient {

	@GetMapping("/user/getSelectedAddress/{addressId}")
	ResponseEntity<AddressDTO> getSelectedAddress(@RequestHeader("Authorization") String token,
			@PathVariable("addressId") String addressId);
	
	@GetMapping("/user/getUserByToken")
	ResponseEntity<UserDTO> getUserByToken(@RequestHeader("Authorization") String token);
	
	@GetMapping("/user/getAllAddress")
	ResponseEntity<List<AddressDTO>> getAllAddress(@RequestHeader("Authorization") String token);
	
}
