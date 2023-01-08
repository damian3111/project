package com.example.project.service;

import com.example.project.entity.Post;
import com.example.project.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    public Post getById(Long id){return postRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));}

    public int setContentById(Long id, String content){return postRepository.setContentById(id, content);}

    public void deletePostById(Long id){
         postRepository.deleteById(id);
    }

    public Post insertPost(Post post){
        return postRepository.save(post);
    }
}
