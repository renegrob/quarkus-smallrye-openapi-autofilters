package io.quarkiverse.smallrye.openapi.extras.test;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Path("my-resource")
@MyPermission("basic-permission")
public class MyFruitResource {

    private static final List<Fruit> list = new ArrayList<>();

    @GET
    @MyDefaultSummary
    List<Fruit> list() {
        return list;
    }

    @MyPermission({ "deletion" })
    @DELETE
    void delete() {
        list.clear();
    }

    // TODO: Kaboom!
    //    @MyPermission({ "modification" })
    //    @PUT
    //    void addFruit(@MyAdjustParameterExample("{ \"name\":\"Lemon\" }") Fruit fruit) {
    //        list.add(fruit);
    //    }

    @MyPermission({ "modification" })
    @PUT
    void addFruits(List<Fruit> fruits) {
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
