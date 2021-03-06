package com.falmeida.tech.springrestsample.service;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.hateoas.EntityModel;
//import org.springframework.hateoas.server.mvc.ControllerLinkBuilder;
//import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.falmeida.tech.springrestsample.model.Post;
import com.falmeida.tech.springrestsample.model.PostRepository;
import com.falmeida.tech.springrestsample.model.User;
import com.falmeida.tech.springrestsample.model.UserDAOImpl;
import com.falmeida.tech.springrestsample.model.UserRepository;

@SuppressWarnings("deprecation")
@RestController
public class UserResource {

	@Autowired
	private UserDAOImpl userDAO;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@GetMapping(path="/users")
	public List<User> retrieveAllUsers(){
		return userRepository.findAll();
	}
	
//	@GetMapping("/users/{id}")
//	public EntityModel<User> retrieveUser(@PathVariable int id) {
//		Optional<User> user = userRepository.findById(id);
//		if(!user.isPresent()) {
//			throw new UserNotFoundException("id: " + id);
//		}
//		EntityModel<User> model = new EntityModel<>(user.get());
//		 
//		WebMvcLinkBuilder linkTo = WebMvcLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
//	 
//		model.add(linkTo.withRel("all-users"));
//	 
//		return model;
//	}
	
	@PostMapping("/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		User createdUser = userRepository.save(user);
		URI location = ServletUriComponentsBuilder
					.fromCurrentRequest()
					.path("/{id}")
					.buildAndExpand(createdUser.getId())
					.toUri();
		return ResponseEntity.created(location).build();
	}
	
	@DeleteMapping("/users/{id}")
	public void deleteUser(@PathVariable int id) {
		userRepository.deleteById(id);
	}
	
	@GetMapping("/users/{id}/posts")
	public List<Post> retrievePostsByUser(@PathVariable int id){
		Optional<User> user = userRepository.findById(id);
		if(!user.isPresent()) {
			throw new UserNotFoundException("id: "+id);
		}
		return user.get().getPosts();
	}
	
	@PostMapping("/users/{id}/posts")
	public ResponseEntity<Object> createUserPost(@PathVariable int id, @RequestBody Post post){
		Optional<User> userOptional = userRepository.findById(id);
		if(!userOptional.isPresent()) {
			throw new UserNotFoundException("id: "+id);
		}
		
		User user = userOptional.get();
		post.setUser(user);
		postRepository.save(post);
		
		URI location = ServletUriComponentsBuilder.
				fromCurrentRequest().
				path("/{id}").
				buildAndExpand(post.getId()).toUri();
		
		return ResponseEntity.created(location).build();
	}
	
	public User deleteById(int id) {
		List<User> users = userDAO.findAll();
		Iterator<User> iterator = users.iterator();
		while(iterator.hasNext()) {
			User user = iterator.next();
			if(user.getId() == id) {
				iterator.remove();
				return user;
			}
		}
		return null;
	}
	
}
