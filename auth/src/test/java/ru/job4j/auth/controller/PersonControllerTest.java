package ru.job4j.auth.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerTest {
    @MockBean
    PersonRepository persons;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    public void shouldReturnAllPersons() throws Exception {
        List<Person> personList = Arrays.asList(
                Person.of("alex", "123"),
                Person.of("julio", "345"));
        String expectedJson = this.mapToJson(personList);
        Mockito.when(persons.findAll()).thenReturn(personList);
        MvcResult result = this.mockMvc.perform(get("/person/")
                .accept(MediaType.APPLICATION_JSON)).andReturn();
        String response1 = result.getResponse().getContentAsString();
        assertThat(response1, is(expectedJson));
        assertThat(HttpStatus.OK.value(), is(result.getResponse().getStatus()));

    }

    @Test
    @WithMockUser
    public void shouldAddPerson() throws Exception {
        Person person = Person.of("alex", "123");
        String inputInJson = this.mapToJson(person);
        Mockito.when(persons.save(Mockito.any(Person.class))).thenReturn(person);
        MvcResult result = mockMvc.perform(post("/person/")
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
        String output = result.getResponse().getContentAsString();
        assertThat(inputInJson, is (output));

    }

    @Test
    @WithMockUser
    public void shouldReturnPersonById() throws Exception {
        Person person = Person.of("alex", "123");
        person.setId(1);
        String inputInJson = this.mapToJson(person);
        Mockito.when(persons.findById(Mockito.anyInt())).thenReturn(java.util.Optional.of(person));
        MvcResult result = mockMvc.perform(get("/person/1")
                .accept(MediaType.APPLICATION_JSON)).andReturn();
        String output = result.getResponse().getContentAsString();
        assertThat(inputInJson, is (output));
    }

    @Test
    @WithMockUser
    public void shouldReturnUpdatePerson() throws Exception {
        Person person = Person.of("alex", "123");
        person.setId(1);
        String inputInJson = this.mapToJson(person);
        mockMvc.perform(put("/person/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputInJson))
                .andExpect(status().isOk());
        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        verify(persons).save(argument.capture());
        assertThat(argument.getValue().getLogin(), is("alex"));
    }

    @Test
    @WithMockUser
    public void shouldPersonDelete() throws Exception {
        this.mockMvc.perform(delete("/person/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
