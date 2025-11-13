package com.cart.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cart.api.ProductFeignClient;
import com.cart.api.UserFeignClient;
import com.cart.dto.AddressDTO;
import com.cart.dto.CartDTO;
import com.cart.dto.OrderDTO;
import com.cart.dto.ProductDTO;
import com.cart.dto.UserDTO;
import com.cart.entity.Cart;
import com.cart.entity.OrderData;
import com.cart.exception.AddressNotFoundException;
import com.cart.exception.CartException;
import com.cart.repository.CartRepository;
import com.cart.repository.OrderRepository;

import jakarta.servlet.http.HttpServletRequest;

import com.cart.exception.UserLoginException;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private DiscoveryClient client;
	
	@Autowired
	ExecutorService executor;
	
	@Autowired
	ProductFeignClient productFeignClient;
	
	@Autowired
	UserFeignClient userFeignClient;
	
	@Override
	public Long createCart(String userId) throws CartException {
		List<Cart> optionalCart = cartRepository.findByUserId(userId);
		if(!optionalCart.isEmpty()) {
			throw new CartException("Cart already exist for the user.");
		}
		Cart cart = new Cart(userId);
		cartRepository.save(cart);
		return cart.getCartId();
	}

	@Override
	public String addProductToCart(String productId, HttpServletRequest request) throws CartException, UserLoginException {
		UserDTO userDTO = this.validateToken(request);
		Optional<Cart> optionalCart = cartRepository.findById(userDTO.getCartId());
		Cart cart = optionalCart.orElseThrow();
		ResponseEntity<ProductDTO> res = productFeignClient.getProductDetails(request.getHeader("Authorization"), productId);
		if(!res.getStatusCode().is2xxSuccessful()) {
			throw new CartException("Product cannot be added to cart.");
		}
		
		ProductDTO productDTO = res.getBody();
		String products = "";
		String[] productArray = cart.getProducts().split(",");
		
		if(cart.getProducts().trim().equals("")) {
			products = productId + "_1";
		} else {
			HashMap<String, Integer> map = new HashMap<>();
			for(String product: productArray) {
				String[] prodArr = product.split("_");
				map.put(prodArr[0], Integer.valueOf(prodArr[1]));
			}
			map.put(productId, map.getOrDefault(productId, 0) + 1);
			products = map.entrySet().stream()
	                .map(e -> e.getKey() + "_" + e.getValue())
	                .collect(Collectors.joining(","));;
		}
		
		cart.setProducts(String.join(",", products));
		cart.setTotalAmount(cart.getTotalAmount() + productDTO.getPrice());
		cart.setDiscountedAmount(cart.getDiscountedAmount()
				+ (productDTO.getPrice()*(1 - productDTO.getDiscount()/100)));
		cartRepository.flush();
		return "Product successfully added to cart";
	}

	@Override
	public String removeProductFromCart(String productId, HttpServletRequest request) throws CartException, UserLoginException {		
		UserDTO userDTO = this.validateToken(request);
		Cart cart = cartRepository.findById(userDTO.getCartId()).get();
		if(!cart.getProducts().contains(productId)) {
			throw new CartException("Product is not in cart.");
		}
		ResponseEntity<ProductDTO> res = productFeignClient.getProductDetails(request.getHeader("Authorization"), productId);
		if(!res.getStatusCode().is2xxSuccessful()) {
			throw new CartException("Product cannot remove from cart.");
		}
		
		ProductDTO productDTO = res.getBody();
		String[] productArray = cart.getProducts().split(",");
		List<String> prodList = Arrays.asList(productArray);
		List<String> newProdList = prodList.stream().map(p->{
			String[] prodArr = p.split("_");
			if(prodArr[0].equals(productId)) {
				Integer count = Integer.parseInt(prodArr[1]);
				if (count == 1) {
					return "remove";
				}else {
					return productId + "_" + String.valueOf(count - 1);
				}
			}else {
				return p;
			}
		}).filter(p-> !p.equals("remove")).collect(Collectors.toList());
		
		String products = newProdList.stream().collect(Collectors.joining(","));
		cart.setProducts(products);
		cart.setTotalAmount(cart.getTotalAmount() - productDTO.getPrice());
		cart.setDiscountedAmount(cart.getDiscountedAmount()
				- (productDTO.getPrice()*(1 - productDTO.getDiscount()/100)));
		cartRepository.flush();
		return "Product remove successfully.";
	}

	@Override
	public CartDTO getCartDetails(HttpServletRequest request) throws UserLoginException {
		UserDTO userDTO = this.validateToken(request);
		Cart cart = cartRepository.findById(userDTO.getCartId()).get();
		if(cart.getProducts().equals("")) {
			return CartDTO.entityToDTO(cart, new ArrayList<ProductDTO>());
		}
		String[] productArray = cart.getProducts().split(",");
		HashMap<String, Long> productMap = Arrays.stream(productArray).map(p-> p.split("_"))
		.collect(Collectors.toMap(p-> p[0], p-> Long.parseLong(p[1]), (a,b)->b, HashMap::new));
		
		Set<String> productSet = productMap.keySet();
		ResponseEntity<List<ProductDTO>> dtoList = productFeignClient.selectedProducts(request.getHeader("Authorization"), productSet);
		for(ProductDTO dto: dtoList.getBody()) {
			dto.setQuantity(productMap.get(dto.getProductId()));
		}
		CartDTO cartDTO = CartDTO.entityToDTO(cart, dtoList.getBody());
	
		return cartDTO;
	}
	
	@Override
	public String buyCartedProduct(HttpServletRequest request) throws CartException, UserLoginException, InterruptedException, ExecutionException {
		UserDTO userDTO = this.validateToken(request);
		Cart cart = cartRepository.findById(userDTO.getCartId()).get();
		if(cart.getProducts().equals("")) {
			throw new CartException("Cart is empty");
		}
		if(cart.getSelectedAddressId() == null) {
			throw new CartException("Please select address.");
		}
		String[] productArray = cart.getProducts().split(",");
		HashMap<String, Long> productMap = Arrays.stream(productArray).map(p-> p.split("_"))
				.collect(Collectors.toMap(p-> p[0], p-> Long.parseLong(p[1]), (a,b)->b, HashMap::new));
		
		ResponseEntity<AddressDTO> addressEntity = userFeignClient.getSelectedAddress(request.getHeader("Authorization"), String.valueOf(cart.getSelectedAddressId()));
		AddressDTO addressDTO = addressEntity.getBody();
		
		OrderData order = new OrderData();
		order.setAmount(cart.getDiscountedAmount());
		order.setUserId(userDTO.getUserId());
		order.setProducts(cart.getProducts());
		order.setOrderDate(LocalDate.now());
		order.setPincode(addressDTO.getPincode());
		order.setCity(addressDTO.getCity());
		order.setStreetNumber(addressDTO.getStreetNumber());
		order.setOrderStatus("Pending");
		
		orderRepository.save(order);
		
		ResponseEntity<Boolean> flag = productFeignClient.buySelectedProducts(request.getHeader("Authorization"), productMap);
		if (!flag.getBody()) {
			order.setOrderStatus("Unsuccessful");
			orderRepository.flush();
			throw new CartException("Purches unsuccesful");
		}
		order.setOrderStatus("Successful");
		orderRepository.flush();
		
		cart.resetCart();
		cartRepository.flush();
		
		return "Order purchase successfully.";
	}

	@Override
	public String buySpecificProduct(String productId, String quantity, HttpServletRequest request) throws CartException, UserLoginException, InterruptedException, ExecutionException {		
		UserDTO userDTO = this.validateToken(request);
		Cart cart = cartRepository.findById(userDTO.getCartId()).get();
		
		HashMap<String, Long> productMap = new HashMap<>();
		productMap.put(productId, Long.parseLong(quantity));
		
		HttpHeaders header = getHeaderWithAuth(request);
		Future<ResponseEntity<AddressDTO>> futureAddressEntity = this.getSelectedAddress(header, cart.getSelectedAddressId());
		Future<ResponseEntity<ProductDTO>> futureProdRes = this.getProductDetails(header, productId);		
		
		ResponseEntity<AddressDTO> addressEntity = futureAddressEntity.get();
		AddressDTO addressDTO = addressEntity.getBody();
		
		ResponseEntity<ProductDTO> prodRes = futureProdRes.get();
		ProductDTO product = prodRes.getBody();
		
		OrderData order = new OrderData();
		order.setAmount(product.getPrice() * (1.0 - product.getDiscount()/100));
		order.setUserId(userDTO.getUserId());
		order.setProducts(productId + "_" + quantity);
		order.setOrderDate(LocalDate.now());
		order.setPincode(addressDTO.getPincode());
		order.setCity(addressDTO.getCity());
		order.setStreetNumber(addressDTO.getStreetNumber());
		order.setOrderStatus("Pending");
		
		orderRepository.save(order);
		
		ResponseEntity<Boolean> flag = productFeignClient.buySelectedProducts(request.getHeader("Authorization"), productMap);
		if (!flag.getBody()) {
			order.setOrderStatus("Unsuccessful");
			orderRepository.flush();
			throw new CartException("Purches unsuccesful");
		}
		order.setOrderStatus("Successful");
		orderRepository.flush();
		
		return "Order purchase successfully.";
	}

	@Override
	public List<OrderDTO> getOrderDetails(HttpServletRequest request) throws com.cart.exception.UserLoginException {
		UserDTO userDTO = this.validateToken(request);
		List<OrderData> orderList = orderRepository.findByUserId(userDTO.getUserId());
		List<OrderDTO> orderDtoList = new ArrayList<>();
		for(OrderData order: orderList) {
			OrderDTO orderDto = OrderDTO.entityToDTO(order);
			String[] productArray = order.getProducts().split(",");
			HashMap<String, Long> productMap = Arrays.stream(productArray).map(p-> p.split("_"))
			.collect(Collectors.toMap(p-> p[0], p-> Long.parseLong(p[1]), (a,b)->b, HashMap::new));
			
			Set<String> productSet = productMap.keySet();
			
			ResponseEntity<List<ProductDTO>> dtoList = productFeignClient.selectedProducts(request.getHeader("Authorization"), productSet);
			
			List<ProductDTO> productDtoList = dtoList.getBody();
			for(ProductDTO productDto: productDtoList) {
				productDto.setQuantity(productMap.get(productDto.getProductId()));
			}
			orderDto.setProducts(productDtoList);
			
			orderDtoList.add(orderDto);
		}
		return orderDtoList;
	}
	
	@Override
	public UserDTO validateToken(HttpServletRequest request) throws UserLoginException {
		HttpHeaders header = this.getHeaderWithAuth(request);
		ResponseEntity<UserDTO> dto = null;
		try{
			String userURI = client.getInstances("UserService").get(0).getUri().toString();
			String url = userURI + "/user/getUserByToken";
			dto = new RestTemplate().exchange(url, 
											  HttpMethod.GET, 
											  new HttpEntity<>(header), 
											  UserDTO.class);
		} catch(Exception e) {
			System.out.println("Exception : " + e.getMessage());
			throw new com.cart.exception.UserLoginException("User has been logged out, Please login again.");
		} finally {
			System.out.println("token validation end");
		}
		return dto.getBody(); 
	}
	
	private HttpHeaders getHeaderWithAuth(HttpServletRequest request) throws UserLoginException {
		String authHeader = request.getHeader("Authorization");
		if(authHeader == null || authHeader.equals("") || !authHeader.startsWith("Bearer ")) {
			throw new UserLoginException("User has been logged out, Please login again.");
		}
		HttpHeaders headers = new HttpHeaders();

		headers.set("Authorization", authHeader);
		return headers;
	}

	@Override
	public Boolean changeAddress(HttpServletRequest request, Long addressId) throws CartException, UserLoginException, AddressNotFoundException {
		UserDTO userDTO = this.validateToken(request);

		ResponseEntity<List<AddressDTO>> resEnt = userFeignClient.getAllAddress(request.getHeader("Authorization"));
		List<AddressDTO> addressList = resEnt.getBody();
		
		Boolean flag = false;
		for(AddressDTO dto: addressList) {
			if(dto.getAddressId().equals(addressId))
				flag = true;
		}
		if(!flag)
			throw new AddressNotFoundException("Address not present.");
		
		List<Cart> carts = cartRepository.findByUserId(userDTO.getUserId());
		carts.get(0).setSelectedAddressId(addressId);
		
		cartRepository.flush();
		return true;
	}
	
	private Future<ResponseEntity<AddressDTO>> getSelectedAddress(HttpHeaders header, Long addressId){
		String userAddressURL = "http://UserService/user/getSelectedAddress/" + addressId;
		return executor.submit(()-> restTemplate.exchange(userAddressURL,
				   										  HttpMethod.GET,
				   										  new HttpEntity<>(header),
				   										  AddressDTO.class)) ;
	}
	
	private Future<ResponseEntity<ProductDTO>> getProductDetails(HttpHeaders header, String productId){
		String productDetailsURL = "http://ProductService/product/getProductDetails?productId=" + productId;
		return executor.submit(()-> restTemplate.exchange(productDetailsURL,
														  HttpMethod.GET,	
														  new HttpEntity<>(header),
														  ProductDTO.class));
	}
}
