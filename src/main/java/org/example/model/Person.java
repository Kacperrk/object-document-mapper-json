package org.example.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Person {
    private List<String> skills;
    private List<Integer> luckyNumbers;
    private List<Boolean> flags;
    private List<Double> grades;
    private List<Address> previousAddresses;
    private List<List<String>> nestedSkills;
    private List<String> nullElements;
    private List<String> emptyList;
    private List<String> nullableList;
}
