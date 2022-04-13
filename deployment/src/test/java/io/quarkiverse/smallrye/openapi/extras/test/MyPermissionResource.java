package io.quarkiverse.smallrye.openapi.extras.test;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import io.quarkiverse.smallrye.openapi.extras.test.annotations.MyPermission;

@Path("my-permission")
@MyPermission("basic-permission")
public class MyPermissionResource {

    private static final List<Fruit> list = new ArrayList<>();

    @GET
    public List<Fruit> list() {
        return list;
    }

    @MyPermission({ "deletion" })
    @DELETE
    public void delete() {
        list.clear();
    }

    @MyPermission({ "modification" })
    @POST
    public void addFruit(@RequestBody Fruit fruit) {
        list.add(fruit);
    }

    @MyPermission({ "modification" })
    @PUT
    public void addFruits(List<Fruit> fruits) {
        list.addAll(fruits);
    }

    public static class Fruit {

        @Schema(example = "Banana")
        public String name;
        @Schema(example = "Yellow fruit")
        public String description;

        public Fruit() {
        }

        public Fruit(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}
