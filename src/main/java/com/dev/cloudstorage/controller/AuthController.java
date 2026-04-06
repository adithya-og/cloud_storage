package com.dev.cloudstorage.controller;

import com.dev.cloudstorage.dto.LoginRequest;
import com.dev.cloudstorage.dto.LoginResponse;
import com.dev.cloudstorage.dto.SignupRequest;
import com.dev.cloudstorage.dto.SignupResponse;
import com.dev.cloudstorage.model.User;
import com.dev.cloudstorage.repository.UserRepository;
import com.dev.cloudstorage.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
														loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Safely get user details
		Object principal = authentication.getPrincipal();
		if (!(principal instanceof User)) {
			return ResponseEntity.status(401).body("Authentication failed");
		}

		User userDetails = (User) principal;
		String jwt = jwtUtils.generateJwtToken(userDetails);

		return ResponseEntity.ok(new LoginResponse(jwt, userDetails));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest()
					.body(new SignupResponse(false, "Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest()
					.body(new SignupResponse(false, "Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User();
		user.setUsername(signUpRequest.getUsername());
		user.setEmail(signUpRequest.getEmail());
		user.setPassword(encoder.encode(signUpRequest.getPassword()));
		user.setFirstName(signUpRequest.getFirstName());
		user.setLastName(signUpRequest.getLastName());

		userRepository.save(user);

		return ResponseEntity.ok(new SignupResponse(true, "User registered successfully!", user));
	}
}