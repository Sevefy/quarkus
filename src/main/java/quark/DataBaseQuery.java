package quark;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@ApplicationScoped
public class DataBaseQuery {
    @Inject
    DataSource dataSource;

    public Response getAllStudentsJDBC(){
        String sql = "select * from students";
        List<StudentCard> students = new ArrayList<>();
        try (Connection con = this.dataSource.getConnection();
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

    public Response putStudentJDBC(long id, StudentCard student){
        String sql = String.format("update students set name = '%s', surname = '%s' where id = %d",
                student.getName(), student.getSurname(), id);
        try (Connection con = this.dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(student).build();
    }

    public Response deleteStudentJDBC(long id){
        String sql = String.format("delete from students where id = %d",id);
        try (Connection con =  this.dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok().build();
    }

}
