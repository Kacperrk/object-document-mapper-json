package org.example.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.annotations.JsonDefaultValue;
import org.example.annotations.JsonIgnore;
import org.example.annotations.JsonName;
import org.example.annotations.JsonSkipNull;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Person {
    private String name;
    private String lastName;
    private int age;
    private boolean active;
    private double averageGrade;

    private Address address;

    private List<String> skills;
    private List<Integer> luckyNumbers;
    private List<Boolean> flags;
    private List<Double> grades;
    private List<Address> previousAddresses;
    private List<List<String>> nestedSkills;
    private List<String> nullElements;
    private List<String> emptyList;
    private List<String> nullableList;

    @JsonName("pseudonim")
    private String nick;

    @JsonIgnore
    private String password;

    @JsonSkipNull
    private String email;

    @JsonDefaultValue("nieznany")
    private String country;
}
