package io.quarkiverse.smallrye.openapi.extras.test;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkiverse.smallrye.openapi.extras.test.annotations.MyAdjustRequestBodyExample;
import io.quarkiverse.smallrye.openapi.extras.test.annotations.MyDefaultSummary;
import io.quarkiverse.smallrye.openapi.extras.test.annotations.MyPermission;

@Path("my-fruit-resource")
@MyPermission("basic-permission")
public class MyFruitResource {

    private static final List<Fruit> list = new ArrayList<>();

    @GET
    @MyDefaultSummary
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
    public void addFruit(
            @MyAdjustRequestBodyExample("{ \"name\":\"Lemon\" }") Fruit fruit) {
        list.add(fruit);
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
