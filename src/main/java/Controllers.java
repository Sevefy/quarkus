import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.annotation.processing.Messager;
import javax.sql.DataSource;
import java.util.*;
@ApplicationScoped
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Controllers {

    //работа с базой
    @Inject
    MeterRegistry meterRegistry;

    private List<StudentCard> studentCardList = new ArrayList<>();

    public Controllers(MeterRegistry meterRegistry) throws InterruptedException {
        this.meterRegistry = meterRegistry;
        studentCardList.add(StudentCard.builder().surname("Malofeev").name("Arseniy").build());
        meterRegistry.gaugeCollectionSize("student.card.list.size", Tags.empty(),studentCardList);
        meterRegistry.gauge("database.size", StudentCard.findAll().list().size());
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
        return new DataBaseQuery().getAllStudentsJDBC();
    }



    @POST
    @Transactional
    @Path("/post_student")
    public Response addStudent(StudentCard student){
        meterRegistry.counter("add.student.counter").increment();
        System.out.println(student);
        student.persist();
        meterRegistry.gauge("database.size", StudentCard.findAll().list().size());
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
        meterRegistry.gauge("database.size", StudentCard.findAll().list().size());
        return Response.ok().status(200).build();
    }


    @PUT
    @Transactional
    @Path("/put_student_jdbc/{id}")
    public Response putStudent2(@PathParam("id") long id, StudentCard student)
    {
        return new DataBaseQuery().putStudentJDBC(id,student);
    }


    @DELETE
    @Transactional
    @Path("/delete_student_jdbc/{id}")
    public Response deleteStudent2(@PathParam("id") long id)
    {
        Response response = new DataBaseQuery().deleteStudentJDBC(id);
        meterRegistry.gauge("database.size", StudentCard.findAll().list().size());
        return response;
    }

    //работа с листом
    //@Path("/get_student_card_object")
    //@GET
    //public StudentCard getClass() {return new StudentCard("Arseniy","Malofeev");}


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
    public StudentCard postObjectGetObject(StudentCard mc){
        System.out.print(mc);
        if (!Objects.equals(mc.getName(), "") || !Objects.equals(mc.getSurname(), "")){
            studentCardList.add(mc);
        }
        meterRegistry.gaugeCollectionSize("student.card.list.size", Tags.empty(),studentCardList);
        return mc;
    }
    @Path("/post_student_card/objects")
    @POST
    public List<StudentCard> postObjectGetList(StudentCard mc){
        System.out.print(mc);
        if (!Objects.equals(mc.getName(), "") || !Objects.equals(mc.getSurname(), "")){
            studentCardList.add(mc);
        }
        meterRegistry.gaugeCollectionSize("student.card.list.size", Tags.empty(),studentCardList);
        return studentCardList;
    }
    @Path("/post_student_cards/object")
    @POST
    public StudentCard postObjectGetList(List<StudentCard> mc){
        System.out.print(mc);
        studentCardList.addAll(mc);
        meterRegistry.gaugeCollectionSize("student.card.list.size", Tags.empty(),studentCardList);
        return mc.get(0);
    }

    @Path("/post_collection/objects")
    @POST
    public List<StudentCard> postCollection(StudentCard mc){
        if (!Objects.equals(mc.getName(), "") || !Objects.equals(mc.getSurname(), "")){
            studentCardList.add(mc);
        }
        meterRegistry.gaugeCollectionSize("student.card.list.size", Tags.empty(),studentCardList);
        return studentCardList;
    }

    @Path("/put_collection/objects")
    @PUT
    public List<StudentCard> putCollection(StudentCard mc){
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
    public List<StudentCard> deleteCollection(StudentCard mc){
        StudentCard finded = (StudentCard) studentCardList.stream()
                .filter(StudentCard -> ((Objects.equals(StudentCard.getName(), mc.getName())) &&
                        (Objects.equals(StudentCard.getSurname(), mc.getSurname())))).findFirst()
                .orElse(null);
        //System.out.println(findedList);
        if (finded != null){
            studentCardList.remove(finded);
        }
        meterRegistry.gaugeCollectionSize("student.card.list.size", Tags.empty(),studentCardList);
        return studentCardList;
    }

    @Path("/get_collection/objects")
    @GET
    public List<StudentCard> getCollection(){
        return studentCardList;
    }

// работа с очередью


    @Inject
    ChannelRabbit channelRabbit;
    @Path("/send_message-rabbit")
    @POST
    public String sendMessage(JsonObject json) throws InterruptedException {
        channelRabbit.setSendMessage(json.values().toString());
        channelRabbit.sendMessage();
        return channelRabbit.getNewMessage();
    }


}
