package com.cart.api;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.cart.dto.ProductDTO;

@FeignClient(name = "ProductService", url = "http://localhost:9999/")
public interface ProductFeignClient {

	@GetMapping("/product/getProductDetails")
	ResponseEntity<ProductDTO> getProductDetails(@RequestHeader("Authorization") String token,@RequestParam("productId") String productId);
	
	@PostMapping("/product/selectedProducts")
	ResponseEntity<List<ProductDTO>> selectedProducts(@RequestHeader("Authorization") String token,@RequestBody Set<String> productSet);
	
	@PostMapping("/product/buySelectedProducts")
	ResponseEntity<Boolean> buySelectedProducts(@RequestHeader("Authorization") String token,@RequestBody HashMap<String, Long> productMap);
	
}
