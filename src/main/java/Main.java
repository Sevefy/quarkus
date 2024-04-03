import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class Main {

    @Inject
    DataSource ds;


    @GET
    @Path("/student")
    public Response getAllStudents()
    {
        return Response.ok(StudentCard.findAll().list()).build();
    }

    @GET
    @Path("/student2")
    public Response getAllStudents2()
    {
        String sql = "select * from students";
        List<StudentCard> students = new ArrayList<>();
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString(2);
                    String lastname = rs.getString(3);
                    long id = rs.getLong(1);
                    StudentCard curStudent = StudentCard.builder().surname(lastname).name(name).build();
                    curStudent.id = id;
                    students.add(curStudent);
                    Log.info(curStudent);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(students).build();
    }



    @POST
    @Transactional
    @Path("/student")
    public Response addStudent(StudentCard student){
        System.out.println(student);
        student.persist();
        return Response.ok(student).status(200).build();
    }

    @PUT
    @Transactional
    @Path("/student/{id}")
    public Response putStudent(@PathParam("id") long id, StudentCard student){
        StudentCard curStudent = StudentCard.findById(id);
        if (curStudent == null) {
            throw new WebApplicationException("Person with id of " + id + " does not exist.", 404);
        }
        if(student.getName() != null) {
            curStudent.setName(student.getName());
        }
        if(student.getSurname() != null) {
            curStudent.setSurname(student.getSurname());
        }
        curStudent.persist();
        return Response.ok(curStudent).status(200).build();
    }

    @DELETE
    @Transactional
    @Path("/student/{id}")
    public Response deleteStudent(@PathParam("id") long id)
    {
        StudentCard.deleteById(id);
        return Response.ok().status(200).build();
    }


    @PUT
    @Transactional
    @Path("/student2/{id}")
    public Response putStudent2(@PathParam("id") long id, StudentCard student)
    {
        String sql = String.format("update students set name = '%s', surname = '%s' where id = %d",
                student.getName(), student.getSurname(), id);
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(student).build();
    }


    @DELETE
    @Transactional
    @Path("/student2/{id}")
    public Response deleteStudent2(@PathParam("id") long id)
    {
        String sql = String.format("delete from students where id = %d",id);
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok().build();
    }

}
