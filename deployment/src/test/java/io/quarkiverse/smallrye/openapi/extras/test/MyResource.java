package io.quarkiverse.smallrye.openapi.extras.test;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.quarkiverse.smallrye.openapi.extras.test.annotations.MyDefaultSummary;
import io.quarkiverse.smallrye.openapi.extras.test.annotations.MyPermission;

@Path("my-resource")
@MyPermission("basic-permission")
public class MyResource {

    private static List<String> list = List.of("A", "B", "C");

    @GET
    @MyDefaultSummary
    public List<String> list() {
        return list;
    }

    @MyPermission({ "deletion", "admin" })
    @DELETE
    public void delete() {
        list = List.of();
    }
}
