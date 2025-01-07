package io.github.pedro.ifc.quarkussocial.rest;

import io.github.pedro.ifc.quarkussocial.domain.model.Post;
import io.github.pedro.ifc.quarkussocial.domain.repository.FollowerRepository;
import io.github.pedro.ifc.quarkussocial.domain.repository.PostRepository;
import io.github.pedro.ifc.quarkussocial.domain.repository.UserRepository;
import io.github.pedro.ifc.quarkussocial.domain.model.User;
import io.github.pedro.ifc.quarkussocial.rest.dto.CreatePostRequest;
import io.github.pedro.ifc.quarkussocial.rest.dto.CreateUserRequest;
import io.github.pedro.ifc.quarkussocial.rest.dto.PostResponse;
import io.github.pedro.ifc.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private final UserRepository userRepository;
    private final PostRepository repository;
    private final Validator validator;
    private final FollowerRepository followerRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository repository, Validator validator, FollowerRepository followerRepository){
        this.userRepository = userRepository;
        this.repository = repository;
        this.validator= validator;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request){
        User user = userRepository.findById(userId);
        if (user == null)
            return  Response.status(Response.Status.NOT_FOUND).build();

        Set<ConstraintViolation<CreatePostRequest>> violations = validator.validate(request);

        if(!violations.isEmpty()){
            return ResponseError.createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        Post post = new Post();
        post.setPost_text(request.getPost_text());
        post.setUser(user);

        repository.persist(post);

        return Response.status(Response.Status.CREATED).entity(post).build();
    }

    @GET
    public Response listPosts( @PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId){
        User user = userRepository.findById(userId);
        if (user == null)
            return  Response.status(Response.Status.NOT_FOUND).build();
        User follower = userRepository.findById(followerId);
        if (follower == null)
            return  Response.status(Response.Status.NOT_FOUND).build();

        if(!this.followerRepository.follows(follower, user))
            return Response.status(Response.Status.FORBIDDEN).build();

        PanacheQuery<Post> query =  repository.
                find("user",
                    Sort.by("dateTime", Sort.Direction.Descending),
                user);

        List<Post> list = query.list();

        List<PostResponse> listFiltered = list.stream()
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(listFiltered).build();
    }
}
