//package marxo.dao;
//
//import com.fasterxml.jackson.core.JsonGenerationException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import junit.framework.Assert;
//import marxo.bean.Project;
//import marxo.data.JsonParser;
//import marxo.restlet.ProjectRestlet;
//import marxo.tool.DataGenerator;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.UUID;
//
//public class ProjectDaoTest {
//	ProjectDao projectDao;
//	ArrayList<Project> projectList = new ArrayList<Project>();
//
//	@BeforeClass
//	public void setUp() {
//		projectDao = new ProjectDao();
//	}
//
//	@Test(groups = {"project-create"})
//	public void addProject() {
//		try {
//			Project project = new Project();
//			project.setName(DataGenerator.getRandomProjectName());
//			project.setModifiedDate(new Date());
//			project.setCreatedDate(new Date());
//
//			String projectString = JsonParser.getMapper().writeValueAsString(project);
//			System.out.println("Create project: " + projectString);
//
//			Assert.assertTrue(projectDao.create(project));
//			projectList.add(project);
//
//		} catch (JsonMappingException e) {
//			e.printStackTrace();
//		} catch (JsonGenerationException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Test(groups = {"project-read"}, dependsOnGroups = {"project-create"})
//	public void getProject() {
//		UUID projectId = projectList.get(projectList.size() - 1).getId();
//		Project project = projectDao.read(projectId);
//		Assert.assertNotNull(project);
//	}
//
//	@Test(groups = {"project-update"}, dependsOnGroups = {"project-read"})
//	public void updateProject() {
//		Project project = projectList.get(projectList.size() - 1);
//		String name = DataGenerator.getRandomProjectName();
//		project.setName(name);
//
//		Assert.assertTrue(projectDao.update(project));
//
//		Project project1 = projectDao.read(project.getId());
//		Assert.assertEquals(name, project1.getName());
//	}
//
//	@Test(groups = {"project-delete"}, dependsOnGroups = {"project-update"})
//	public void deleteProject() {
//		Project project = projectList.get(projectList.size() - 1);
//
//		Assert.assertTrue(projectDao.delete(project.getId()));
//
//		projectList.remove(project);
//	}
//
//	@Test(groups = {"parsing"}, dependsOnGroups = {"project-create"})
//	public void testParseProject() {
//		Project project = projectList.get(projectList.size() - 1);
//		ProjectRestlet projectRestlet = new ProjectRestlet();
//		String projectJson = projectRestlet.getProject(project.getId().toString());
//
//		System.out.println("Project retrived:\n" + projectJson);
//
//		try {
//			project = JsonParser.getMapper().readValue(projectJson, Project.class);
//
//			Assert.assertNotNull(project);
//		} catch (IOException e) {
//			e.printStackTrace();
//			Assert.fail();
//		}
//	}
//}
