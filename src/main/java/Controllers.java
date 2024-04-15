import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.sql.DataSource;
import java.util.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Controllers {
    @Inject
    DataSource dataSource;
    //работа с базой


    private List<StudentCard> studentCardList = new ArrayList<>();

    public Controllers(){
        studentCardList.add(StudentCard.builder().surname("Malofeev").name("Arseniy").build());
    }

    @GET
    @Path("/get_all_controllers")
    public Response getAllControllers(){
        Map<String,String> controllers = new HashMap<>();
        controllers.put("/get_students", " - запрос на получение всех студентов");
        controllers.put("/get_students_jdbc", " - запрос на получение всех студентов (запрос к базе выполнен с помощью jdbc)");
        controllers.put("/post_student", " - запрос на добавление студента");
        controllers.put("/put_student/{id}", " - запрос на обновление данных студента по его id");
        controllers.put("/delete_student", " - запрос на удаление студента по id");
        controllers.put("/put_student_jdbc/{id}", " - запрос на обновление данных студента по его id(запрос к базе выполнен с помощью jdbc)");
        controllers.put("/delete_student_jdbc/{id}", " - запрос на удаление студента по id(запрос к базе выполнен с помощью jdbc)");
        controllers.put("/{text}", " - при вводе запроса, который не содержится в списке выводит текст запроса");
        controllers.put("/post_student_card", " - запрос на добавление студента (добавляет студента не в базу, а в лист)");
        controllers.put("/post_student_card/objects", " - запрос на добавление студента, возвращает список студентов содеражащийся в листе");
        controllers.put("/post_student_cards/object", " - запрос на добавление списка студентов, возвращает первый добавленный объект");
        controllers.put("/post_collection/objects", " - запрос на отправку студента, возвращает список студентов");
        controllers.put("/put_collection/objects", " - запрос на обновление объекта, поиск по имени и фамилии, возврат списка объектов");
        controllers.put("/delete_collection/objects", " - запрос на удаление объекта, поиск по имени и фамилии, возврат списка объектов");
        controllers.put("/get_collection/objects", " - запрос на получение списка объектов");
        controllers.put("/get_student_card_object", " - дает объект студента");
        controllers.put("/send_message-rabbit", " - отправка сообщения в очередь сообщений");
        return Response.ok(controllers).build();
    }

    @GET
    @Path("/get_students")
    public Response getAllStudents()
    {
        return Response.ok(StudentCard.findAll().list()).build();
    }

    @GET
    @Path("/get_students_jdbc")
    public Response getAllStudents2()
    {
        return new DataBaseQuery(dataSource).getAllStudentsJDBC();
    }



    @POST
    @Transactional
    @Path("/post_student")
    public Response addStudent(StudentCard student){
        System.out.println(student);
        student.persist();
        return Response.ok(student).status(200).build();
    }

    @PUT
    @Transactional
    @Path("/put_student/{id}")
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
    @Path("/delete_student/{id}")
    public Response deleteStudent(@PathParam("id") long id)
    {
        StudentCard.deleteById(id);
        return Response.ok().status(200).build();
    }


    @PUT
    @Transactional
    @Path("/put_student_jdbc/{id}")
    public Response putStudent2(@PathParam("id") long id, StudentCard student)
    {
        return new DataBaseQuery(dataSource).putStudentJDBC(id,student);
    }


    @DELETE
    @Transactional
    @Path("/delete_student_jdbc/{id}")
    public Response deleteStudent2(@PathParam("id") long id)
    {
        return new DataBaseQuery(dataSource).deleteStudentJDBC(id);
    }

    //работа с листом
    @Path("/get_student_card_object")
    @GET
    public StudentCard get_Class() {return new StudentCard("Arseniy","Malofeev");}


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{text}")
    public String getText(@PathParam("text") String text) {
        return text;
    }

    @Path("/get_query_param")
    @GET
    @Produces({ MediaType.TEXT_PLAIN })
    public String getByQueryParam(
            @QueryParam("place") String place) {
        return "query param = " + place;
    }

    @Path("/post_student_card")
    @POST
    public StudentCard post_object_object(StudentCard mc){
        System.out.print(mc);
        if (!Objects.equals(mc.getName(), "") || !Objects.equals(mc.getSurname(), "")){
            studentCardList.add(mc);
        }
        return mc;
    }
    @Path("/post_student_card/objects")
    @POST
    public List<StudentCard> post_object_list(StudentCard mc){
        System.out.print(mc);
        if (!Objects.equals(mc.getName(), "") || !Objects.equals(mc.getSurname(), "")){
            studentCardList.add(mc);
        }
        return studentCardList;
    }
    @Path("/post_student_cards/object")
    @POST
    public StudentCard post_object_list(List<StudentCard> mc){
        System.out.print(mc);
        studentCardList.addAll(mc);
        return mc.get(0);
    }

    @Path("/post_collection/objects")
    @POST
    public List<StudentCard> post_collection(StudentCard mc){
        if (!Objects.equals(mc.getName(), "") || !Objects.equals(mc.getSurname(), "")){
            studentCardList.add(mc);
        }
        return studentCardList;
    }

    @Path("/put_collection/objects")
    @PUT
    public List<StudentCard> put_collection(StudentCard mc){
        StudentCard finded = (StudentCard) studentCardList.stream()
                .filter(StudentCard -> ((Objects.equals(StudentCard.getName(), mc.getName())) &&
                        (Objects.equals(StudentCard.getSurname(), mc.getSurname())))).findFirst()
                .orElse(null);
        //System.out.println(findedList);
        if (finded != null){
            studentCardList.set(studentCardList.indexOf(finded), mc);
        }
        return studentCardList;
    }

    @Path("/delete_collection/objects")
    @DELETE
    public List<StudentCard> delete_collection(StudentCard mc){
        StudentCard finded = (StudentCard) studentCardList.stream()
                .filter(StudentCard -> ((Objects.equals(StudentCard.getName(), mc.getName())) &&
                        (Objects.equals(StudentCard.getSurname(), mc.getSurname())))).findFirst()
                .orElse(null);
        //System.out.println(findedList);
        if (finded != null){
            studentCardList.remove(finded);
        }
        return studentCardList;
    }

    @Path("/get_collection/objects")
    @GET
    public List<StudentCard> get_collection(){
        return studentCardList;
    }

// работа с очередью

    @Default
    ChannelRabbit channelRabbit;

    @Path("/send_message-rabbit")
    @POST
    public String send_message(JsonObject json) {
        channelRabbit.setCurrentMessage(json);
        return channelRabbit.getNew_message();
    }




}
