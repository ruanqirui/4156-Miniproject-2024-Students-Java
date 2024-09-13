package dev.coms4156.project.individualproject;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
// import org.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for the RouteController class.
 */
@WebMvcTest(RouteController.class) 
public class RouteControllerUnitTests {
  
  /** Testing using Mockito. */
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MyFileDatabase myFileDatabase; 

  @InjectMocks
  private RouteController routeController;

  /** Testing setup.*/
  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(routeController).build();
    HashMap<String, Department> initialMockData = new HashMap<>();
    HashMap<String, Course> courses = new HashMap<>(); 
    courses.put("3261", new Course("CS Example", "417 IAB", "2:40-3:55", 10));
    Department csDepartment = new Department("COMS", courses, "Josh Alman", 100);
    initialMockData.put("COMS", csDepartment);

    // Debug: Print or log to verify the setup
    when(myFileDatabase.getDepartmentMapping()).thenReturn(initialMockData);
    // System.out.println("Mock setup data: " + myFileDatabase.getDepartmentMapping());
  }

  @Test
  public void testIndex() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Welcome")));
  }

  @Test
  public void testRetrieveNoneDepartment() throws Exception {
    // when(myFileDatabase.getDepartmentMapping()).thenReturn(new HashMap<>());
    mockMvc.perform(get("/retrieveDept?deptCode=Happy"))
      .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Department Not Found")));
  }

  @Test
  public void testRetrieveDepartment() throws Exception {
    // System.out.println("Mocked Department Data: " + myFileDatabase.getDepartmentMapping());
    mockMvc.perform(get("/retrieveDept?deptCode=COMS"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers
          .containsString("Instructor: Ansaf Salleb-Aouissi;")));
  }

  @Test
  public void testRetrieveNoneCourse() throws Exception {
    mockMvc.perform(get("/retrieveCourse?deptCode=COMS&courseCode=1001"))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Course Not Found")));
  }

  @Test
  public void testRetrieveCourse() throws Exception {
    mockMvc.perform(get("/retrieveCourse?deptCode=COMS&courseCode=3251"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Tony Dear")));
  }

  @Test
  public void testIsCourseNotFull() throws Exception {
    mockMvc.perform(get("/isCourseFull?deptCode=COMS&courseCode=3251"))
        .andExpect(status().isOk())
        .andExpect(content().string("false")); 
  }

  @Test
  public void testGetMajorCountFromDept() throws Exception {
    mockMvc.perform(get("/getMajorCountFromDept?deptCode=COMS"))
      .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("2700")));
  }

  @Test
  public void testGetMajorCountFromNoneDept() throws Exception {
    mockMvc.perform(get("/getMajorCountFromDept?deptCode=Happy"))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Department Not Found")));
  }

  @Test
  public void testDropStudentNoneCourse() throws Exception {
    mockMvc.perform(patch("/dropStudentFromCourse?deptCode=COMS&courseCode=1000"))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Course Not Found")));
  }

  @Test
  public void testSetEnrollmentCountCourse() throws Exception {
    mockMvc.perform(patch("/setEnrollmentCount?deptCode=COMS&courseCode=3251&count=46"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers
          .containsString("Attributed was updated successfully.")));
  }

  @Test
  public void testSetEnrollmentCountNoneCourse() throws Exception {
    mockMvc.perform(patch("/setEnrollmentCount?deptCode=COMS&courseCode=1000&count=40"))
      .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Course Not Found")));
  }

  @Test
  public void testChangeCourseTimeCourseFound() throws Exception {
    mockMvc.perform(patch("/changeCourseTime?deptCode=COMS&courseCode=3251&time=8:30-10:30"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers
          .containsString("Attributed was updated successfully.")));
  }

  @Test
  public void testChangeCourseTimeNoneCourse() throws Exception {
    mockMvc.perform(patch("/changeCourseTime?deptCode=COMS&courseCode=7999&time=10:00-11:00"))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Course Not Found")));
  }

  @Test
  public void testChangeCourseTeacher() throws Exception {
    mockMvc.perform(patch("/changeCourseTeacher?deptCode=COMS&courseCode=3827&teacher=NewProf"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers
          .containsString("Attributed was updated successfully.")));
  }

  @Test
  public void testChangeCourseNoneTeacher() throws Exception {
    mockMvc.perform(patch("/changeCourseTeacher?deptCode=COMS&courseCode=8282&teacher=NewProf"))
          .andExpect(status().isNotFound())
          .andExpect(content().string(org.hamcrest.Matchers.containsString("Course Not Found")));
  }

  @Test
  public void testChangeCourseLocation() throws Exception {
    mockMvc.perform(patch("/changeCourseLocation?deptCode=COMS&courseCode=3827&location=Mudd 2024"))
          .andExpect(status().isOk())
          .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Attributed was updated successfully.")));
  }

  @Test
  public void testChangeCourseNoneLocation() throws Exception {
    mockMvc.perform(patch("/changeCourseLocation?deptCode=COMS&courseCode=9999&location=Mudd 2024"))
          .andExpect(status().isNotFound())
          .andExpect(content().string(org.hamcrest.Matchers.containsString("Course Not Found")));
  }

  @Test
  public void testFindCourseLocation() throws Exception {
    mockMvc.perform(get("/findCourseLocation?deptCode=COMS&courseCode=3203"))
          .andExpect(status().isOk())
          .andExpect(content().string(org.hamcrest.Matchers
            .containsString("301 URIS is where the course is located.")));
  }

  @Test
  public void testFindCourseNoneLocation() throws Exception {
    mockMvc.perform(get("/findCourseLocation?deptCode=COMS&courseCode=7919"))
          .andExpect(status().isNotFound())
          .andExpect(content().string(org.hamcrest.Matchers.containsString("Course Not Found")));
  }

  @Test
  public void testFindCourseInstructor() throws Exception {
    mockMvc.perform(get("/findCourseInstructor?deptCode=COMS&courseCode=3157"))
          .andExpect(status().isOk())
          .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Jae Lee is the instructor for the course.")));
  }

}
