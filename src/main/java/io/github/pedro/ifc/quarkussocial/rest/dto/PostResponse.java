package io.github.pedro.ifc.quarkussocial.rest.dto;

import io.github.pedro.ifc.quarkussocial.domain.model.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {
    private String post_text;
    private LocalDateTime dateTime;

    public PostResponse(String post_text, LocalDateTime dateTime) {
        this.post_text = post_text;
        this.dateTime = dateTime;
    }

    public static  PostResponse fromEntity(Post post){
        return new PostResponse(post.getPost_text(), post.getDateTime());
    }
}
