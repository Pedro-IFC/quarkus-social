package io.github.pedro.ifc.quarkussocial.rest;

import io.github.pedro.ifc.quarkussocial.domain.model.Follower;
import io.github.pedro.ifc.quarkussocial.domain.model.User;
import io.github.pedro.ifc.quarkussocial.domain.repository.FollowerRepository;
import io.github.pedro.ifc.quarkussocial.domain.repository.UserRepository;
import io.github.pedro.ifc.quarkussocial.rest.dto.FollowerRequest;
import io.github.pedro.ifc.quarkussocial.rest.dto.FollowerResponse;
import io.github.pedro.ifc.quarkussocial.rest.dto.FollowersPerUserResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowersResource {

    private final UserRepository userRepository;
    private final FollowerRepository repository;

    @Inject
    public FollowersResource(UserRepository userRepository, FollowerRepository repository){
        this.userRepository = userRepository;
        this.repository = repository;
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId) {
        User user = userRepository.findById(userId);
        if(user==null){
            return  Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Follower> list = repository.findByUser(userId);
        FollowersPerUserResponse response = new FollowersPerUserResponse();
        response.setFollowersCount(list.size());

        response.setContent(
            list.stream().map(FollowerResponse::new).collect(Collectors.toList())
        );

        return Response.ok(response).build();
    }

    @PUT
    @Transactional
    public Response follow(@PathParam("userId") Long userId, FollowerRequest request) {
        if(userId.equals(request.getFollowerId())){
            return Response.status(Response.Status.CONFLICT).entity("You can't follow yourself").build();
        }

        User user = userRepository.findById(userId);
        if(user==null){
            return  Response.status(Response.Status.NOT_FOUND).build();
        }

        User follower = userRepository.findById(request.getFollowerId());
        if(follower==null){
            return  Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!repository.follows(follower, user)){
            Follower entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);

            repository.persist(entity);
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Transactional
    public Response deleteFollower(@PathParam("userId") Long userId , @QueryParam("followerId") Long followerId) {
        if(userId.equals(followerId)){
            return Response.status(Response.Status.CONFLICT).entity("You can't unfollow yourself").build();
        }

        User user = userRepository.findById(userId);
        if(user==null){
            return  Response.status(Response.Status.NOT_FOUND).build();
        }

        User follower = userRepository.findById(followerId);
        if(follower==null){
            return  Response.status(Response.Status.NOT_FOUND).build();
        }

        if (repository.follows(follower, user)){
            repository.deleteFollowerAndUser(followerId, userId);
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
