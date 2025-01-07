package io.github.pedro.ifc.quarkussocial.domain.repository;

import io.github.pedro.ifc.quarkussocial.domain.model.Follower;
import io.github.pedro.ifc.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user){

        Map<String, Object> params = Parameters.with("follower", follower).and("user", user).map();

        return  this.find("follower = :follower and user = :user", params).count()>0;
    }

    public List<Follower> findByUser(Long userId){
        return this.find("user.id", userId).list();
    }

    public void deleteFollowerAndUser(Long followerId, Long userId){
        Map<String, Object> param = Parameters
                .with("userId", userId)
                 .and("followerId", followerId)
                .map();
        this.delete("follower.id =:followerId and user.id =:userId", param);
    }
}
