package ru.job4j.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {

    @NotNull(message = "id must be not null")
    private Integer id;

    @NotBlank(message = "login must be not empty")
    private String login;

}
