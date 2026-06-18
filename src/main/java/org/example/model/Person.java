package org.example.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.annotations.JsonDefaultValue;
import org.example.annotations.JsonIgnore;
import org.example.annotations.JsonName;
import org.example.annotations.JsonSkipNull;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Person {
    @JsonName("imie")
    private String firstName;

    @JsonIgnore
    private String password;

    @JsonSkipNull
    private String email;

    @JsonDefaultValue("nieznane")
    private String city;
}
