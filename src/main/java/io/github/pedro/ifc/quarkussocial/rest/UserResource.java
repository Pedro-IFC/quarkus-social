package io.github.pedro.ifc.quarkussocial.rest;

import io.github.pedro.ifc.quarkussocial.domain.model.User;
import io.github.pedro.ifc.quarkussocial.domain.repository.UserRepository;
import io.github.pedro.ifc.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.github.pedro.ifc.quarkussocial.rest.dto.CreateUserRequest;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserRepository repository;
    private final Validator validator;

    @Inject
    public UserResource(UserRepository repository, Validator validator){
        this.repository = repository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser( CreateUserRequest userRequest){

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);

        if(!violations.isEmpty()){
            return ResponseError.createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();

        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        repository.persist(user);

        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser( @PathParam("id") Long id){
        User user = repository.findById(id);
        if(user!=null){
            repository.delete(user);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Transactional
    @Path("{id}")
    public Response updateUser( @PathParam("id") Long id, CreateUserRequest userRequest){
        User user = repository.findById(id);
        if(user!=null){
            user.setName(userRequest.getName());
            user.setAge(userRequest.getAge());
            repository.persist(user);

            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    public Response listAllUsers(){
        PanacheQuery<User> query =  repository.findAll();
        return Response.ok(query.list()).build();
    }
}
